package com.ke.hs_tracker.module.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.db.Game
import com.ke.hs_tracker.module.db.GameDao

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
import kotlinx.coroutines.flow.*
import java.io.InputStream
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val parseDeckCodeUseCase: ParseDeckCodeUseCase,
    private val getAllCardUseCase: GetAllCardUseCase,
    private val getLogDirUseCase: GetRealLogDirUseCase,
    private val gameDao: GameDao
) : ViewModel() {
    private var user: PowerTag.GameState.PlayerMapping? = null
    private var opponent: PowerTag.GameState.PlayerMapping? = null

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


    private val _graveyardCardList = MutableStateFlow<List<GraveyardCard>>(emptyList())

    val graveyardCardList: StateFlow<List<GraveyardCard>>
        get() = _graveyardCardList

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


    private val powerParser = PowerParserImpl()


    private var allCard: List<Card> = emptyList()

    private var deckCardList: List<CardBean> = emptyList()

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

        tag.toString().log()

        when (tag) {
            is PowerTag.GameState.BuildNumber -> {

                game = Game(buildNumber = tag.number)
                game.userDeckName = currentDeck?.name ?: ""
                game.userDeckCode = currentDeck?.code ?: ""

            }
            is PowerTag.GameState.FormatType -> {
                game.formatType = tag.type
            }
            is PowerTag.GameState.GameType -> {
                game.gameType = tag.type
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
                "游戏开始了".log()
                _deckLeftCardList.value = deckCardList
                game.startTime = System.currentTimeMillis()
            }
            is PowerTag.PowerTaskList.FullEntity -> {
//                handleFullEntity(tag)
                handleFullEntity(tag)
            }

            is PowerTag.PowerTaskList.ShowEntity -> {
                handleShowEntity(tag)
            }
            is PowerTag.PowerTaskList.TagChange -> {
                handleTagChange(tag)
            }
        }
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

        if (tagChange.entity.player == user?.id && tagChange.entity.zone == Zone.Hand && tagChange.tag == "ZONE" && tagChange.value == "DECK") {
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


    private fun onGameOver() {
        viewModelScope.launch {
            //保存游戏
            game.endTime = System.currentTimeMillis()
            gameDao.insert(game)
            delay(1000)
            clearPowerFile()
        }
        _deckLeftCardList.value = deckCardList

//        _userGraveyardCardList.value = emptyList()
//        _opponentGraveyardCardList.value = emptyList()
        userGraveyardCardList.clear()
        opponentGraveyardCardList.clear()
        _graveyardCardList.value = emptyList()
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
            removeCardFromDeck(showEntity.cardId)
        } else if (showEntity.entity.player == user?.id && showEntity.entity.zone == Zone.Deck && showEntity.payloads["ZONE"].equals(
                "GRAVEYARD",
                true
            )
        ) {
            //爆牌
            // ShowEntity(entity=Entity(entityName=UNKNOWN ENTITY, gameCardType=Invalid, id=54, zone=Deck, zonePosition=0, cardId=null, player=2), cardId=OG_176, payloads={CONTROLLER=2, CARDTYPE=SPELL, TAG_LAST_KNOWN_COST_IN_HAND=3, COST=3, ZONE=GRAVEYARD, ENTITY_ID=54, RARITY=COMMON, 478=2, 1037=2, 1043=1, 1068=0, SPAWN_TIME_COUNT=1, SPELL_SCHOOL=6})
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
        val cardId = entity.cardId
//            ?: throw RuntimeException("没有id $entity")
        if (cardId == null) {
            "发现了一个没有id的卡牌插入到墓地 $entity".log()
            return
        }
        val card = findCardById(cardId)

        if (card.type == CardType.Enchantment) {
//            "衍生牌 $card 不能放到墓地去".log()
            return
        }

//        "插入一张牌到墓地 $card $entity".log()

        //TAG_CHANGE Entity=[entityName=破霰元素 id=62 zone=PLAY zonePos=1 cardId=AV_260 player=2] tag=ZONE value=GRAVEYARD
        val isUser = entity.player == user?.id
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

    private fun onDeckCardsChanged(cardId: String, insert: Boolean) {

        val card = findCardById(cardId)

//        "牌库中的一张卡牌数量发生了变化 $card ，是否是插入 $insert".log()

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
        private suspend fun updateCardList(
            card: Card,
            mutableStateFlow: MutableStateFlow<List<CardBean>>,
            insert: Boolean,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ) = withContext(dispatcher) {
            if (card.type == CardType.Enchantment) {
                return@withContext
            }
            val list = mutableStateFlow.value.toMutableList()
            val bean = list.find {
                it.card.id == card.id
            }
//            if (bean?.card?.type == CardType.Enchantment) {
//                //忽略衍生牌
//                return@withContext
//            }
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