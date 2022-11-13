package com.ke.hs_tracker.module.parser

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.ke.hs_tracker.module.domain.GetAllCardUseCase
import com.ke.hs_tracker.module.domain.GetRealLogDirUseCase
import com.ke.hs_tracker.module.domain.ParseDeckCodeUseCase
import com.ke.hs_tracker.module.entity.*
import com.ke.hs_tracker.module.log
import com.ke.hs_tracker.module.ui.main.powerFileName
import com.ke.mvvm.base.data.successOr
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.InputStream
import javax.inject.Inject

/**
 * 剩余卡牌监听器
 */
interface DeckCardObserver {

    /**
     * 牌库的卡牌
     */
    val deckCardList: StateFlow<List<CardBean>>

    /**
     * 初始化
     */
    fun init(scope: CoroutineScope)
}

class DeckCardObserverImpl @Inject constructor(
    private val powerParser: PowerParser,
    private val powerTagHandler: PowerTagHandler,
    private val getAllCardUseCase: GetAllCardUseCase,
    private val parseDeckCodeUseCase: ParseDeckCodeUseCase,
    private val getLogDirUseCase: GetRealLogDirUseCase,
    @ApplicationContext private val context: Context
) : DeckCardObserver {


//    private val _userGraveyardCardList = MutableStateFlow<List<GraveyardCard>>(emptyList())
//
//   override val userGraveyardCardList: StateFlow<List<GraveyardCard>>
//        get() = _userGraveyardCardList

    /**
     * 当前用户的卡组
     */
    private var currentUserDeck: CurrentDeck? = null

    private val _deckCardList =
        MutableStateFlow<List<CardBean>>(emptyList())
//        Channel<List<CardBean>>(capacity = Channel.CONFLATED)


    override val deckCardList: StateFlow<List<CardBean>>
        get() = _deckCardList

    /**
     * 所有卡牌
     */
    private var allCards = emptyList<Card>()

    /**
     * 当前卡组的卡牌
     */
    private var currentDeckList: List<CardBean> = emptyList()

    /**
     * 当前卡组剩余的卡牌
     */
    private var deckLeftCardList: List<CardBean> = listOf()
        set(value) {
//            "设置了剩余卡牌 ${
//                value.map {
//                    it.count
//                }.reduce { acc, i ->
//                    acc + i
//                }
//            }".log()
            var count = 0
            value.forEach {
                count += it.count
            }
            "设置了剩余卡牌 $count".log()
            field = value
        }

    /**
     * 获取炉石log文件夹
     */
    private suspend fun getLogsDir(): DocumentFile? {
        return getLogDirUseCase(Unit).successOr(null)
    }

    /**
     * 获取文件流
     */
    private suspend fun getFileStream(fileName: String): InputStream? =
        withContext(Dispatchers.IO) {
            val documentFile = getLogsDir()?.findFile(fileName)
            if (documentFile == null) {
                "无法访问 $fileName 文件".log()

                return@withContext null
            }

            context.contentResolver.openInputStream(documentFile.uri)
        }

    override fun init(
        scope: CoroutineScope,
    ) {
        val interval = 1500L

        scope.launch {
            clearPowerLogFile()
        }

        scope.launch {
            ///获取所有卡牌
            allCards = getAllCardUseCase(Unit).successOr(emptyList())
        }

        val deckFileObserver = DeckFileObserver(interval) {
            getFileStream("Decks.log")
        }

        val powerFileObserver = PowerFileObserver(interval) {
            getFileStream(powerFileName)
        }

        scope.launch {
            //监听牌库
            delay(1000)
            deckFileObserver
                .start()
                .flowOn(Dispatchers.IO)
                .map {
//                currentUserDeck = it
                    parseDeckCodeUseCase(it.code).successOr(emptyList())
                }.collect {
                    currentDeckList = it
                    _deckCardList.value = it.toList()
//                _deckCardList.send(it)
                }
        }

        scope.launch {
            powerTagHandler.gameEventFlow.collect {
                when (it) {


                    is GameEvent.OnGameOver -> {


                        clearPowerLogFile()
                        "清空卡牌 OnGameOver ,deckLeftCardList ${deckLeftCardList.size} , currentDeckList ${currentDeckList.size} , _deckCardList = ${_deckCardList.value.size}".log()

//                        deckLeftCardList.clear()
//                        deckLeftCardList.addAll(currentDeckList)
                        deckLeftCardList = currentDeckList.toList()
                        _deckCardList.value = deckLeftCardList.toList()
//                        _deckCardList.send(deckLeftCardList)

                        "清空卡牌 OnGameOver ,deckLeftCardList ${deckLeftCardList.size} , currentDeckList ${currentDeckList.size} , _deckCardList = ${_deckCardList.value.size}".log()

                        it.game.apply {
                            userDeckCode = currentUserDeck?.code ?: ""
                            userDeckName = currentUserDeck?.name ?: ""
                        }

                        powerFileObserver.reset()
                    }
                    GameEvent.OnGameStart -> {
//                        deckLeftCardList = currentDeckList
                        "清空卡牌 OnGameStart ,deckLeftCardList ${deckLeftCardList.size} , currentDeckList ${currentDeckList.size}".log()
//                        deckLeftCardList.clear()
//                        deckLeftCardList.addAll(currentDeckList)
                        deckLeftCardList = currentDeckList.toList()

                        _deckCardList.value = deckLeftCardList.toList()
//                        _deckCardList.send(deckLeftCardList)
                    }
                    is GameEvent.RemoveCardFromUserDeck -> {
                        onUserDeckCardListChanged(it.cardId, true)
                    }

                    is GameEvent.InsertCardToUserDeck -> {
                        onUserDeckCardListChanged(it.cardId, false)
                    }
                    GameEvent.None -> {

                    }
                }
            }
        }

        powerParser.powerTagListener = {
            powerTagHandler.handle(it)
        }



        scope.launch {

            powerFileObserver.start()
                .flowOn(Dispatchers.IO)
                .collect { list ->
                    list.forEach {
                        powerParser.parse(it)
                    }
                }
        }

    }

    /**
     * 清空log文件
     */
    private suspend fun clearPowerLogFile() {
        val documentFile = getLogsDir()?.findFile(powerFileName)
        documentFile?.apply {
            context.contentResolver.openOutputStream(uri, "wt")?.use {
                it.write("".encodeToByteArray())
                it.flush()
                it.close()
            }
        }
    }

    /**
     * 用户牌库的卡牌发生了变化
     */
    private fun onUserDeckCardListChanged(cardId: String, remove: Boolean) {


        val card = allCards.find {
            it.id == cardId
        } ?: throw IllegalArgumentException("找不到id是 $cardId 的卡牌")



        if (card.type == CardType.Enchantment) {
            return
        }

        "牌库的卡牌发生了变化 $card $remove ".log()

        val bean = deckLeftCardList.find {
            it.card.id == card.id
        }


        val list = mutableListOf<CardBean>()
        list.addAll(deckLeftCardList)


        if (bean == null) {
            list.add(CardBean(card, 1))
        } else {
//            bean.count =
            val newCount = if (remove) bean.count - 1 else bean.count + 1
            list[deckLeftCardList.indexOf(bean)] = bean.updateCount(newCount)
        }

//        if (bean?.count == 3) {
//            "插入了3张进去？ ".log()
//        }

        val newList = list.sortedBy {
            it.card.cost
        }.filter {
            it.count > 0
        }
//        deckLeftCardList.clear()
//        deckLeftCardList.addAll(newList)
        deckLeftCardList = newList
        _deckCardList.value = deckLeftCardList.toList()
//        _deckCardList.send(deckLeftCardList)
    }


}