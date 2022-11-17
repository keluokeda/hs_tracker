package com.ke.hs_tracker.module.parser

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.ke.hs_tracker.module.db.GameDao
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
     * 自己的墓地
     */
    val userGraveyardCardList: StateFlow<List<CardBean>>

    /**
     * 对手的墓地
     */
    val opponentGraveyardCardList: StateFlow<List<CardBean>>

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
    private val gameDao: GameDao,
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

    private val _userGraveyardCardList = MutableStateFlow<List<CardBean>>(emptyList())

    override val userGraveyardCardList: StateFlow<List<CardBean>>
        get() = _userGraveyardCardList

    private val _opponentGraveyardCardList = MutableStateFlow<List<CardBean>>(emptyList())
    override val opponentGraveyardCardList: StateFlow<List<CardBean>>
        get() = _opponentGraveyardCardList

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
                    null -> {

                    }

                    is GameEvent.OnGameOver -> {

                        _userGraveyardCardList.value = emptyList()
                        _opponentGraveyardCardList.value = emptyList()

                        clearPowerLogFile()

                        deckLeftCardList = currentDeckList.toList()
                        _deckCardList.value = deckLeftCardList.toList()
//                        _deckCardList.send(deckLeftCardList)


                        it.game.apply {
                            userDeckCode = currentUserDeck?.code ?: ""
                            userDeckName = currentUserDeck?.name ?: ""
                            scope.launch {
                                gameDao.insert(this@apply)
                            }
                        }




                        powerFileObserver.reset()
                    }
                    GameEvent.OnGameStart -> {

                        _userGraveyardCardList.value = emptyList()
                        _opponentGraveyardCardList.value = emptyList()
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

                    is GameEvent.InsertCardToGraveyard -> {
                        onGraveyardCardsChanged(it.cardId, it.isUser)
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

    private fun onGraveyardCardsChanged(cardId: String, isUser: Boolean) {

        //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=106 zone=PLAY zonePos=0 cardId= player=1] tag=ZONE value=GRAVEYARD
        //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=5 zone=SECRET zonePos=0 cardId= player=1] tag=COST value=2
        //如果对面打出一张奥秘拍 会直接进入墓地
//            ?: throw RuntimeException("没有id $entity")

        val card = allCards.find {
            it.id == cardId
        } ?: return

        if (card.type == CardType.Enchantment) {
//            "衍生牌 $card 不能放到墓地去".log()
            return
        }

//        "插入一张牌到墓地 $card $entity".log()

        //TAG_CHANGE Entity=[entityName=破霰元素 id=62 zone=PLAY zonePos=1 cardId=AV_260 player=2] tag=ZONE value=GRAVEYARD
        if (isUser) {
            _userGraveyardCardList.value += CardBean(card, 1)
        } else {
            _opponentGraveyardCardList.value += CardBean(card, 1)
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