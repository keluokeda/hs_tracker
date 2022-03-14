package com.ke.hs_tracker.module.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.domain.GetAllCardUseCase
import com.ke.hs_tracker.module.domain.GetRealLogDirUseCase
import com.ke.hs_tracker.module.domain.ParseDeckCodeUseCase
import com.ke.hs_tracker.module.entity.*
import com.ke.hs_tracker.module.log
import com.ke.hs_tracker.module.parser.DeckFileObserver
import com.ke.hs_tracker.module.parser.PowerFileObserver
import com.ke.hs_tracker.module.parser.PowerParserImpl
import com.ke.mvvm.base.data.successOr
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.io.InputStream
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val parseDeckCodeUseCase: ParseDeckCodeUseCase,
    private val getAllCardUseCase: GetAllCardUseCase,
    private val getLogDirUseCase: GetRealLogDirUseCase
) : ViewModel() {
    private lateinit var user: PowerTag.GameState.PlayerMapping
    private lateinit var opponent: PowerTag.GameState.PlayerMapping

//    private var logsDir: DocumentFile? = null

    private suspend fun getLogsDir(): DocumentFile? {
        return getLogDirUseCase(Unit).successOr(null)
//        return context.findHSDataFilesDir("Logs")
    }

    private val _title = MutableStateFlow("标题")

    val title: StateFlow<String>
        get() = _title

    private val _deckLeftCardList = MutableStateFlow<List<CardBean>>(emptyList())

    val deckLeftCardList: StateFlow<List<CardBean>>
        get() = _deckLeftCardList


    //用户卡牌墓地
    private val _userGraveyardCardList = MutableStateFlow<List<CardBean>>(emptyList())

    val userGraveyardCardList: StateFlow<List<CardBean>>
        get() = _userGraveyardCardList

    //对手卡牌墓地
    private val _opponentGraveyardCardList = MutableStateFlow<List<CardBean>>(emptyList())

    val opponentGraveyardCardList: StateFlow<List<CardBean>>
        get() = _opponentGraveyardCardList

    private val powerFileObserver: PowerFileObserver by lazy {
        PowerFileObserver {
            getFileStream("Power.log")
        }

    }
    private val deckFileObserver: DeckFileObserver by lazy {
        DeckFileObserver {
            getFileStream("Decks.log")
        }
    }


    private val powerParser = PowerParserImpl()


    private lateinit var allCard: List<Card>

    private lateinit var deckCardList: List<CardBean>

    init {

        viewModelScope.launch {
            clearPowerFile()
            allCard = getAllCardUseCase(Unit).successOr(emptyList())

        }




        powerParser.powerTagListener = {
            handlePowerTag(it)
        }
        viewModelScope.launch {
            delay(1000)
            deckFileObserver
                .start()
                .map {
                    it to parseDeckCodeUseCase(it.code).successOr(emptyList())

                }
                .collect {
                    _deckLeftCardList.value = it.second
                    _title.value = it.first.name
                    deckCardList = it.second
                }
        }
        viewModelScope.launch {
            powerFileObserver.start()
                .collect {
                    it.forEach { line ->
                        powerParser.parse(line)
                    }
                }
        }
    }

    private fun handlePowerTag(tag: PowerTag) {


        when (tag) {
            is PowerTag.GameState.BuildNumber -> {

            }
            is PowerTag.GameState.FormatType -> {

            }
            is PowerTag.GameState.GameType -> {

            }
            is PowerTag.GameState.PlayerMapping -> {
                if (tag.isUser) {
                    user = tag
                } else {
                    opponent = tag
                }

            }
            is PowerTag.GameState.ScenarioID -> {

            }
            is PowerTag.PowerTaskList.Block -> {
                tag.list.forEach {
                    handlePowerTag(it)
                }

            }
            is PowerTag.PowerTaskList.CreateGame -> {
                //初始化卡牌
                "游戏开始了".log()
                _deckLeftCardList.value = deckCardList
            }
            is PowerTag.PowerTaskList.FullEntity -> {
//                handleFullEntity(tag)
            }
            is PowerTag.PowerTaskList.ShowEntity -> {
                handleShowEntity(tag)
            }
            is PowerTag.PowerTaskList.TagChange -> {
                handleTagChange(tag)
            }
        }
    }


    private fun handleTagChange(tagChange: PowerTag.PowerTaskList.TagChange) {
        if (tagChange.entity.player == user.id && tagChange.entity.zone == Zone.Hand && tagChange.tag == "ZONE" && tagChange.value == "DECK") {
            //TAG_CHANGE Entity=[entityName=冷风 id=15 zone=HAND zonePos=3 cardId=AV_266 player=1] tag=ZONE value=DECK
            insertCardToDeck(tagChange.entity.cardId!!)
        } else if (tagChange.tag == "ZONE" && tagChange.value == "GRAVEYARD" && tagChange.entity.zone == Zone.Play) {
            //TAG_CHANGE Entity=[entityName=破霰元素 id=62 zone=PLAY zonePos=1 cardId=AV_260 player=2] tag=ZONE value=GRAVEYARD
            //随从死亡后进入墓地
            //TAG_CHANGE Entity=[entityName=始生研习 id=63 zone=PLAY zonePos=0 cardId=SCH_270 player=2] tag=ZONE value=GRAVEYARD
            //打出法术
            onGraveyardCardsChanged(tagChange.entity)

        } else if (tagChange.isGameComplete) {
            "游戏结束了".log()
            onGameOver()
        }
    }


    private fun onGameOver() {
        clearPowerFile()
        _deckLeftCardList.value = deckCardList
        powerFileObserver.reset()
        deckFileObserver.reset()
        _userGraveyardCardList.value = emptyList()
        _opponentGraveyardCardList.value = emptyList()
    }


    private fun handleShowEntity(showEntity: PowerTag.PowerTaskList.ShowEntity) {
        if (showEntity.entity.player == user.id && showEntity.entity.zone == Zone.Deck && showEntity.payloads["ZONE"].equals(
                "hand",
                true
            )
        ) {

            //起始手牌
            //SHOW_ENTITY - Updating Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=15 zone=DECK zonePos=0 cardId= player=1] CardID=AV_266
            //    tag=CONTROLLER value=1
            //    tag=CARDTYPE value=SPELL
            //    tag=TAG_LAST_KNOWN_COST_IN_HAND value=1
            //    tag=COST value=1
            //    tag=PREMIUM value=1
            //    tag=ZONE value=HAND
            //    tag=ENTITY_ID value=15
            //    tag=RARITY value=COMMON
            //    tag=478 value=1
            //    tag=1043 value=1
            //    tag=1068 value=0
            //    tag=SPAWN_TIME_COUNT value=1
            //    tag=SPELL_SCHOOL value=3
            removeCardFromDeck(showEntity.cardId)
        }
    }

    private fun removeCardFromDeck(cardId: String) {
        onDeckCardsChanged(cardId, false)
    }

    private fun insertCardToDeck(cardId: String) {
        onDeckCardsChanged(cardId, true)
    }


    private fun onGraveyardCardsChanged(entity: Entity) {

        //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=106 zone=PLAY zonePos=0 cardId= player=1] tag=ZONE value=GRAVEYARD
        //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=5 zone=SECRET zonePos=0 cardId= player=1] tag=COST value=2
        //如果对面打出一张奥秘拍 会直接进入墓地
        val cardId = entity.cardId ?: throw RuntimeException("没有id $entity")
        val cardEntity = findCardById(cardId)

        "插入一张牌到墓地 $cardEntity $entity".log()

        //TAG_CHANGE Entity=[entityName=破霰元素 id=62 zone=PLAY zonePos=1 cardId=AV_260 player=2] tag=ZONE value=GRAVEYARD
        if (entity.player == user.id) {
            viewModelScope.launch {
                updateCardList(
                    cardEntity, _userGraveyardCardList, true
                )
            }

        } else {
            viewModelScope.launch {
                updateCardList(
                    cardEntity, _opponentGraveyardCardList, true
                )
            }

        }
    }

    private fun onDeckCardsChanged(cardId: String, insert: Boolean) {

        val card = findCardById(cardId)

        "牌库中的一张卡牌数量发生了变化 $card ，是否是插入 $insert".log()

//        updateCardList(card, _deckLeftCardList, insert)
        viewModelScope.launch {
            updateCardList(card, _deckLeftCardList, insert)
        }


//        val currentCardList = _deckLeftCardList.value.toMutableList()
//
//        val target = currentCardList.find {
//            it.cardEntity.id == cardId
//        }
//        if (target == null) {
//            //新加入一张卡牌
//            currentCardList.add(CardBean(card, 1))
//        } else {
//            target.count = if (insert) (target.count + 1) else (target.count - 1)
//        }
//        currentCardList.sortBy { it.cardEntity.cost }
//
//        _deckLeftCardList.value = currentCardList.filter {
//            it.count != 0
//        }.apply {
//            val list = map { it.cardEntity.name to it.count }
//            "准备发送的数据是 $list".log()
//        }
    }

    private fun findCardById(cardId: String) =
        allCard.find { it.id == cardId } ?: throw RuntimeException("id为 $cardId 没有这张牌")


    private fun clearPowerFile() {

        viewModelScope.launch {
            val documentFile = getLogsDir()?.findFile(powerFileName)
            documentFile?.apply {
                context.contentResolver.openOutputStream(uri, "wt")?.use {
                    it.write("".encodeToByteArray())
                    it.flush()
                    it.close()
                }
            }
        }


    }

    private suspend fun getFileStream(fileName: String): InputStream? = withContext(dispatcher) {
        val documentFile = getLogsDir()?.findFile(fileName)
        if (documentFile == null) {
            "无法访问 $fileName 文件".log()

            return@withContext null
        }

        context.contentResolver.openInputStream(documentFile.uri)
    }

    companion object {
        private suspend fun updateCardList(
            card: Card,
            mutableStateFlow: MutableStateFlow<List<CardBean>>,
            insert: Boolean,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ) = withContext(dispatcher) {
            val list = mutableStateFlow.value.toMutableList()
            val bean = list.find {
                it.card.id == card.id
            }
            if (bean == null) {
                list.add(CardBean(card, 1))
            } else {
                bean.count = if (insert) bean.count + 1 else bean.count - 1
            }

            mutableStateFlow.value = list.sortedBy {
                it.card.cost
            }.filter {
                it.count != 0
            }
        }
    }
}

private const val powerFileName = "Power.log"