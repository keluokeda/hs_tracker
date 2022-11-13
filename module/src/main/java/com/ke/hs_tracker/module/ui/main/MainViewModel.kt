package com.ke.hs_tracker.module.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.db.*
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.domain.GetAllCardUseCase
import com.ke.hs_tracker.module.domain.GetRealLogDirUseCase
import com.ke.hs_tracker.module.domain.ParseDeckCodeUseCase
import com.ke.hs_tracker.module.domain.SaveLogFileUseCase
import com.ke.hs_tracker.module.entity.*
import com.ke.hs_tracker.module.log
import com.ke.hs_tracker.module.parser.DeckFileObserver
import com.ke.hs_tracker.module.parser.PowerFileObserver
import com.ke.hs_tracker.module.parser.PowerParser
import com.ke.mvvm.base.data.successOr
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.InputStream
import java.util.*
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val parseDeckCodeUseCase: ParseDeckCodeUseCase,
    private val getAllCardUseCase: GetAllCardUseCase,
    private val getLogDirUseCase: GetRealLogDirUseCase,
    private val gameDao: GameDao,
    private val zonePositionChangedEventDao: ZonePositionChangedEventDao,
    private val saveLogFileUseCase: SaveLogFileUseCase,
    private val powerParser: PowerParser
) : ViewModel() {
    private var user: PowerTag.GameState.PlayerMapping? = null
    private var opponent: PowerTag.GameState.PlayerMapping? = null

    private var currentTurn = 0

//    private var logsDir: DocumentFile? = null

    private suspend fun getLogsDir(): DocumentFile? {
        return getLogDirUseCase(Unit).successOr(null)
    }


    private val _title = MutableStateFlow("标题")

    val title: StateFlow<String>
        get() = _title

    private val _deckLeftCardList = MutableStateFlow<List<CardBean>>(emptyList())

    val deckLeftCardList: StateFlow<List<CardBean>>
        get() = _deckLeftCardList


    private val _graveyardCardList = MutableStateFlow<List<GraveyardCard>>(emptyList())

    val graveyardCardList: StateFlow<List<GraveyardCard>>
        get() = _graveyardCardList


    /**
     * 对手手牌
     */
    private val currentOpponentHandCards = mutableListOf<OpponentHandCard>()

    private val _opponentHandCards =
        MutableStateFlow<Pair<String, List<OpponentHandCard>>>("" to emptyList())

    /**
     * 对手手牌
     */
    val opponentHandCards: StateFlow<Pair<String, List<OpponentHandCard>>>
        get() = _opponentHandCards

    private val userGraveyardCardList = mutableListOf<GraveyardCard>()

    private val opponentGraveyardCardList = mutableListOf<GraveyardCard>()


    private val _showUserGraveyardCardList = MutableStateFlow(true)

    /**
     * 是否显示用户墓地
     */
    internal val showUserGraveyardCardList: StateFlow<Boolean>
        get() = _showUserGraveyardCardList


    internal fun toggleShowUserGraveyard(showUser: Boolean) {
        _showUserGraveyardCardList.value = showUser
//        _graveyardCardList.value =
//            if (showUser) userGraveyardCardList else opponentGraveyardCardList
        updateGraveyardCardList()
    }


    private val _sortBy = MutableStateFlow(SortBy.Cost)

//    internal val sortBy: StateFlow<SortBy>
//        get() = _sortBy

    /**
     * 设置排序方式
     */
    internal fun setSort(sortBy: SortBy) {
        _sortBy.value = sortBy
        updateGraveyardCardList()
    }

    private fun updateGraveyardCardList() {
        val source =
            if (_showUserGraveyardCardList.value) userGraveyardCardList else opponentGraveyardCardList

        val result = when (_sortBy.value) {
            SortBy.Cost -> source.sortedBy { it.card.cost }
            SortBy.CostReverse -> source.sortedByDescending { it.card.cost }
            SortBy.Time -> source.sortedBy { it.time }
            SortBy.TimeReverse -> source.sortedByDescending { it.time }
        }
        _graveyardCardList.value = result
    }


    private val powerFileObserver: PowerFileObserver by lazy {
        PowerFileObserver(1500) {
            getFileStream("Power.log")
        }

    }
    private val deckFileObserver: DeckFileObserver by lazy {
        DeckFileObserver {
            getFileStream("Decks.log")
        }
    }

    private var currentDeck: CurrentDeck? = null
    private var game: Game = Game()


//    private val powerParser = PowerParserImpl()


    private var allCard: List<Card> = emptyList()

    private var deckCardList: List<CardBean> = emptyList()

    private val zoneChangedEventList = mutableListOf<ZonePositionChangedEvent>()

    private val entityIdAndCardIdMap = mutableMapOf<Int, String>()

    init {

        viewModelScope.launch {
            clearPowerFile()
            allCard = getAllCardUseCase(Unit).successOr(emptyList())

        }


        viewModelScope.launch {
            _deckLeftCardList.collect {

            }
        }


//        viewModelScope.launch {
//            powerParser.powerTagFlow.collect {
//                handlePowerTag(it)
//            }
//        }

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
                    currentDeck = it.first
                }
        }
        viewModelScope.launch {
            powerFileObserver.start()
                .flowOn(dispatcher)
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

                game = Game(buildNumber = tag.number)
                game.userDeckName = currentDeck?.name ?: ""
                game.userDeckCode = currentDeck?.code ?: ""

            }
            is PowerTag.GameState.FormatType -> {
                game.formatType = tag.type.toFormatType
            }
            is PowerTag.GameState.GameType -> {
                game.gameType = tag.type.toGameType
            }
            is PowerTag.GameState.PlayerMapping -> {

                if (tag.isUser) {
                    game.userName = tag.name
                    user = tag

//                    game.isUserFirst = tag.first
                } else {
                    game.opponentName = tag.name
                    opponent = tag

                }


            }
            is PowerTag.GameState.ScenarioID -> {
                game.scenarioID = tag.id.toInt()
            }
            is PowerTag.PowerTaskList.Block -> {
                tag.list.forEach {
                    handlePowerTag(it)
                }


            }
            is PowerTag.PowerTaskList.CreateGame -> {
                //初始化卡牌
                onGameStarted()
            }
            is PowerTag.PowerTaskList.FullEntity -> {
//                handleFullEntity(tag)
                handleFullEntity(tag)
                //FULL_ENTITY - Updating [entityName=萨尔 id=74 zone=PLAY zonePos=0 cardId=HERO_02 player=2] CardID=HERO_02
                //    tag=CONTROLLER value=2
                //    tag=CARDTYPE value=HERO
                //    tag=HEALTH value=30
                //    tag=ZONE value=PLAY
                //    tag=ENTITY_ID value=74
                //    tag=FACTION value=NEUTRAL
                //    tag=RARITY value=FREE
                //    tag=HERO_POWER value=687
                //    tag=SPAWN_TIME_COUNT value=1
                tag.cardId?.apply {
                    entityIdAndCardIdMap[tag.entity.id] = this
                }
            }

            is PowerTag.PowerTaskList.ShowEntity -> {
                handleShowEntity(tag)
                entityIdAndCardIdMap[tag.entity.id] = tag.cardId
            }
            is PowerTag.PowerTaskList.TagChange -> {
                handleTagChange(tag)

            }
        }


//        tag.toString().log()

        (tag as? ZoneUpdatable)?.apply {


            isUpdateZone(user?.id)?.let {
                val cardId = it.cardId ?: entityIdAndCardIdMap[it.entityId]


//                val event = ZonePositionChangedEvent(
//                    entityId = it.first.id,
//                    cardId = it.first.cardId,
//                    isUser = it.first.player == user?.id,
//                    currentZone = it.first.zone,
//                    newZone = it.second,
//                    currentPosition = it.first.zonePosition,
//                    newPosition = it.first.zonePosition
//                )
                zoneChangedEventList.add(it)
                if (it.currentZone == Zone.Deck && it.newZone != Zone.Deck && it.isUser) {
                    //从牌库中抽取一张卡
                    //SHOW_ENTITY - Updating Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=48 zone=DECK zonePos=0 cardId= player=2]
                    //    tag=CONTROLLER value=2
                    //    tag=CARDTYPE value=SPELL
                    //    tag=TAG_LAST_KNOWN_COST_IN_HAND value=1
                    //    tag=COST value=1
                    //    tag=PREMIUM value=1
                    //    tag=ZONE value=HAND
                    //    tag=ENTITY_ID value=48
                    //    tag=ELITE value=1
                    //    tag=CLASS value=SHAMAN
                    //    tag=RARITY value=LEGENDARY
                    //    tag=478 value=2
                    //    tag=QUEST_PROGRESS_TOTAL value=3
                    //    tag=676 value=1
                    //    tag=839 value=1
                    //    tag=1043 value=1
                    //    tag=1068 value=0
                    //    tag=QUEST_REWARD_DATABASE_ID value=64323
                    //    tag=SPAWN_TIME_COUNT value=1

                    //或者 探底
                    //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=28 zone=DECK zonePos=0 cardId= player=1] tag=1068 value=3
                    //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=28 zone=DECK zonePos=0 cardId= player=1] tag=1068 value=0
                    //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=28 zone=DECK zonePos=0 cardId= player=1] tag=1037 value=1
                    //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=28 zone=DECK zonePos=0 cardId= player=1] tag=ZONE value=HAND
                    //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=28 zone=DECK zonePos=0 cardId= player=1] tag=ZONE_POSITION value=6

                    //探底抽上来的卡是没有cardId的
                    removeCardFromDeck(cardId)

                } else if (it.newZone == Zone.Deck && it.currentZone != Zone.Deck && it.isUser) {

                    //当心探底
                    //SHOW_ENTITY - Updating Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=28 zone=DECK zonePos=0 cardId= player=1] CardID=DED_002
                    //    tag=CONTROLLER value=1
                    //    tag=CARDTYPE value=SPELL
                    //    tag=TAG_LAST_KNOWN_COST_IN_HAND value=2
                    //    tag=COST value=2
                    //    tag=ZONE value=DECK
                    //    tag=ENTITY_ID value=28
                    //    tag=RARITY value=RARE
                    //    tag=DISCOVER value=1
                    //    tag=478 value=1
                    //    tag=1043 value=1
                    //    tag=1068 value=0
                    //    tag=USE_DISCOVER_VISUALS value=1
                    //    tag=SPAWN_TIME_COUNT value=1
                    //    tag=SPELL_SCHOOL value=1
                    //    tag=1711 value=1
                    //    tag=MINI_SET value=1


                    //有牌插入到牌库
                    //TAG_CHANGE Entity=[entityName=冷风 id=70 zone=HAND zonePos=2 cardId=AV_266 player=2] tag=ZONE value=DECK

                    //会出现id为空的情况
                    //FULL_ENTITY - Updating [entityName=UNKNOWN ENTITY [cardType=INVALID] id=17 zone=DECK zonePos=0 cardId= player=1] CardID=
                    //    tag=ZONE value=DECK
                    //    tag=CONTROLLER value=1
                    //    tag=ENTITY_ID value=17
                    insertCardToDeck(cardId)

                } else if (it.currentZone == Zone.Play && it.newZone == Zone.Graveyard) {
                    onGraveyardCardsChanged(cardId, it.isUser)
                } else if (it.newZone == Zone.Hand || it.currentZone == Zone.Hand) {
                    //手牌
                    if (!it.isUser) {
                        handleOpponentHandChanged(it)

                    }
                }
            }
        }


    }

    private var lastZonePositionChangedEvent: ZonePositionChangedEvent? = null

    /**
     * 对手手牌发生变化
     */
    private fun handleOpponentHandChanged(event: ZonePositionChangedEvent) {

        if (lastZonePositionChangedEvent?.entityId == event.entityId && event.currentZone == event.newZone) {
            return
        }

        if (event.newZone == Zone.Hand) {
            //有卡牌入手 可能从牌库抽到手里 也可能改变了位置

            val target = currentOpponentHandCards.firstOrNull {
                it.entityId == event.entityId
            }
            if (target == null) {
                //插入一张卡牌到list中
                currentOpponentHandCards.add(
                    OpponentHandCard(
                        currentTurn, event.entityId, event.newPosition
                    )
                )
            } else {
                target.position = event.newPosition
            }
        } else if (event.currentZone == Zone.Hand) {
            //手牌出去了一张

            val target = currentOpponentHandCards.firstOrNull {
                it.entityId == event.entityId
            }
            if (target == null) {
                //
            } else {
//                target.position = event.newPosition
                currentOpponentHandCards.remove(target)
            }
        }
        lastZonePositionChangedEvent = event
        _opponentHandCards.value = UUID.randomUUID().toString() to currentOpponentHandCards

        "对手手牌发生了变化 $event ,当前回合是 $currentTurn , 对手手牌 $currentOpponentHandCards".log()
    }


    private fun handleFullEntity(fullEntity: PowerTag.PowerTaskList.FullEntity) {
        fullEntity.isUpdateHero()?.apply {
            val cardClass = allCard.find {
                it.id == second
            }?.cardClass ?: return
            if (first == user?.id) {
                game.userHero = cardClass
            } else if (first == opponent?.id) {
                game.opponentHero = cardClass
            }
        }
    }

    private fun handleTagChange(tagChange: PowerTag.PowerTaskList.TagChange) {

        tagChange.isTurnChanged()?.apply {
            currentTurn = this
        }

        if (tagChange.entity.player == user?.id && tagChange.entity.zone == Zone.Hand && tagChange.tag == "ZONE" && tagChange.value == "DECK") {
            //TAG_CHANGE Entity=[entityName=冷风 id=15 zone=HAND zonePos=3 cardId=AV_266 player=1] tag=ZONE value=DECK
//            insertCardToDeck(tagChange.entity.cardId!!)
        } else if (tagChange.tag == "ZONE" && tagChange.value == "GRAVEYARD" && tagChange.entity.zone == Zone.Play) {
            //TAG_CHANGE Entity=[entityName=破霰元素 id=62 zone=PLAY zonePos=1 cardId=AV_260 player=2] tag=ZONE value=GRAVEYARD
            //随从死亡后进入墓地
            //TAG_CHANGE Entity=[entityName=始生研习 id=63 zone=PLAY zonePos=0 cardId=SCH_270 player=2] tag=ZONE value=GRAVEYARD
            //打出法术
//            onGraveyardCardsChanged(tagChange.entity)

        } else if (tagChange.isGameComplete) {
            "游戏结束了".log()
            onGameOver()
        }

        val pair = tagChange.isPlayerWonOrLost
        if (pair != null) {
            "有玩家胜利或失败了 $pair $game".log()
            if (pair.first == game.userName) {
                game.isUserWin = pair.second
            } else {
                game.opponentName = pair.first
            }
        }

        if (tagChange.tag == "FIRST_PLAYER" && tagChange.value == "1") {
            game.isUserFirst = tagChange.entity.entityName == game.userName
        }

    }

    /**
     * 游戏开始
     */
    private fun onGameStarted() {
        "游戏开始了".log()
        _deckLeftCardList.value = deckCardList
        game.startTime = System.currentTimeMillis()
        currentTurn = 0

        currentOpponentHandCards.clear()
        _opponentHandCards.value = UUID.randomUUID().toString() to currentOpponentHandCards
    }

    private fun onGameOver() {
        viewModelScope.launch {
            //保存游戏
            game.endTime = System.currentTimeMillis()
            gameDao.insert(game)
            zonePositionChangedEventDao.insertAll(zoneChangedEventList.map {
                it.cardId = entityIdAndCardIdMap[it.entityId]
                it.gameId = game.id
                it.cardName = allCard.find { card: Card ->
                    it.cardId == card.id
                }?.name

                it
            })
            entityIdAndCardIdMap.clear()
            zoneChangedEventList.clear()
            getFileStream("Power.log")?.apply {
                saveLogFileUseCase(game.id to this)
            }

            delay(1000)
            clearPowerFile()
        }
        _deckLeftCardList.value = deckCardList

//        _userGraveyardCardList.value = emptyList()
//        _opponentGraveyardCardList.value = emptyList()
        userGraveyardCardList.clear()
        opponentGraveyardCardList.clear()
        _graveyardCardList.value = emptyList()
        currentTurn = 0
        _opponentHandCards.value = UUID.randomUUID().toString() to emptyList()
    }


    private fun handleShowEntity(showEntity: PowerTag.PowerTaskList.ShowEntity) {
        if (showEntity.entity.player == user?.id && showEntity.entity.zone == Zone.Deck && showEntity.payloads["ZONE"].equals(
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
//            removeCardFromDeck(showEntity.cardId)
        } else if (showEntity.entity.player == user?.id && showEntity.entity.zone == Zone.Deck && showEntity.payloads["ZONE"].equals(
                "GRAVEYARD",
                true
            )
        ) {
            //爆牌
            // ShowEntity(entity=Entity(entityName=UNKNOWN ENTITY, gameCardType=Invalid, id=54, zone=Deck, zonePosition=0, cardId=null, player=2), cardId=OG_176, payloads={CONTROLLER=2, CARDTYPE=SPELL, TAG_LAST_KNOWN_COST_IN_HAND=3, COST=3, ZONE=GRAVEYARD, ENTITY_ID=54, RARITY=COMMON, 478=2, 1037=2, 1043=1, 1068=0, SPAWN_TIME_COUNT=1, SPELL_SCHOOL=6})
//            removeCardFromDeck(showEntity.cardId)

        }
    }

    private fun removeCardFromDeck(cardId: String?) {
        onDeckCardsChanged(cardId, false)
    }

    private fun insertCardToDeck(cardId: String?) {
        onDeckCardsChanged(cardId, true)
    }


    private fun onGraveyardCardsChanged(cardId: String?, isUser: Boolean) {

        //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=106 zone=PLAY zonePos=0 cardId= player=1] tag=ZONE value=GRAVEYARD
        //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=5 zone=SECRET zonePos=0 cardId= player=1] tag=COST value=2
        //如果对面打出一张奥秘拍 会直接进入墓地
//            ?: throw RuntimeException("没有id $entity")
        if (cardId == null) {
            return
        }
        val card = findCardById(cardId)

        if (card.type == CardType.Enchantment) {
//            "衍生牌 $card 不能放到墓地去".log()
            return
        }

//        "插入一张牌到墓地 $card $entity".log()

        //TAG_CHANGE Entity=[entityName=破霰元素 id=62 zone=PLAY zonePos=1 cardId=AV_260 player=2] tag=ZONE value=GRAVEYARD
        if (isUser) {
//            viewModelScope.launch {
//                updateCardList(
//                    cardEntity, _userGraveyardCardList, true
//                )
//            }
            userGraveyardCardList.add(GraveyardCard(card))

//            _graveyardCardList.value = userGraveyardCardList
        } else {
//            viewModelScope.launch {
//                updateCardList(
//                    cardEntity, _opponentGraveyardCardList, true
//                )
//            }
            opponentGraveyardCardList.add(GraveyardCard(card))
//            _graveyardCardList.value = opponentGraveyardCardList

        }

        if (isUser && _showUserGraveyardCardList.value) {
            //是当前用户的卡插入到墓地 并且显示是当前用户的墓地
//            _graveyardCardList.value = userGraveyardCardList
            updateGraveyardCardList()
        } else if (!isUser && _showUserGraveyardCardList.value) {
//            _graveyardCardList.value = opponentGraveyardCardList
            updateGraveyardCardList()
        }
    }

    private fun onDeckCardsChanged(cardId: String?, insert: Boolean) {

        if (cardId == null) {
            return
        }

        val card = findCardById(cardId)

        updateCardList(card, _deckLeftCardList, insert)

    }

    private fun findCardById(cardId: String) =
        allCard.find { it.id == cardId } ?: throw RuntimeException("id为 $cardId 没有这张牌")


    private suspend fun clearPowerFile() {

        withContext(dispatcher) {
            val documentFile = getLogsDir()?.findFile(powerFileName)
            documentFile?.apply {
                context.contentResolver.openOutputStream(uri, "wt")?.use {
                    it.write("".encodeToByteArray())
                    it.flush()
                    it.close()
                }
            }

            powerFileObserver.reset()
            deckFileObserver.reset()
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
        fun updateCardList(
            card: Card,
            mutableStateFlow: MutableStateFlow<List<CardBean>>,
            insert: Boolean,
        ) {
            if (card.type == CardType.Enchantment) {
                return
            }
            val list = mutableStateFlow.value.toMutableList()
            val bean = list.find {
                it.card.id == card.id
            }

            if (bean == null) {
                list.add(CardBean(card, 1))
            } else {
                bean.count = if (insert) bean.count + 1 else bean.count - 1
            }

            if (bean?.count == 3) {
                "插入了3张进去？ ".log()
            }

            mutableStateFlow.value = list.sortedBy {
                it.card.cost
            }.filter {
                it.count != 0
            }
        }
    }
}

const val powerFileName = "Power.log"

data class OpponentHandCard(
    /**
     * 回合数
     */
    val turn: Int,
    /**
     * 实体id
     */
    val entityId: Int,
    /**
     * 位置
     */
    var position: Int,

    /**
     * 时间
     */
    val time: Long = System.currentTimeMillis()
)