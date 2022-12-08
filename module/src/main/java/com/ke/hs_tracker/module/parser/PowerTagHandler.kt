package com.ke.hs_tracker.module.parser

import com.ke.hs_tracker.module.db.Game
import com.ke.hs_tracker.module.domain.GetAllCardUseCase
import com.ke.hs_tracker.module.entity.*
import com.ke.hs_tracker.module.log
import com.ke.mvvm.base.data.successOr
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * power tag处理器
 */
interface PowerTagHandler {


    /**
     * 处理一个事件
     */
    fun handle(powerTag: PowerTag)


    /**
     * 游戏事件流
     */
    val gameEventFlow: Flow<GameEvent?>
}

class PowerTagHandlerImpl @Inject constructor(
    private val getAllCardUseCase: GetAllCardUseCase
) : PowerTagHandler {

    lateinit var allCard: List<Card>


    init {
        GlobalScope.launch {
            allCard = getAllCardUseCase(Unit).successOr(emptyList())
        }
    }


    private val _gameEventFlow =
//        Channel<GameEvent>(capacity = Channel.UNLIMITED)
        MutableStateFlow<GameEvent?>(null)

    override val gameEventFlow: Flow<GameEvent?>
        get() = _gameEventFlow


    /**
     * 当前卡组
     */
//    var currentDeck: CurrentDeck? = null

    /**
     * 玩家
     */
    private var user: PowerTag.GameState.PlayerMapping? = null

    /**
     * 对手
     */
    private var opponent: PowerTag.GameState.PlayerMapping? = null

    /**
     * 当前回合数
     */
    private var currentTurn = 0


    /**
     * 游戏实体
     */
    private var game: Game = Game()

    private val entityIdAndCardIdMap = mutableMapOf<Int, String>()


    override fun handle(powerTag: PowerTag) {

        when (powerTag) {
            is PowerTag.GameState.BuildNumber -> {

                game = Game(buildNumber = powerTag.number)
//                game.userDeckName = currentDeck?.name ?: ""
//                game.userDeckCode = currentDeck?.code ?: ""

            }
            is PowerTag.GameState.FormatType -> {
                game.formatType = powerTag.type.toFormatType
            }
            is PowerTag.GameState.GameType -> {
                game.gameType = powerTag.type.toGameType
            }
            is PowerTag.GameState.PlayerMapping -> {

                if (powerTag.isUser) {
                    game.userName = powerTag.name
                    user = powerTag

//                    game.isUserFirst = tag.first
                } else {
                    game.opponentName = powerTag.name
                    opponent = powerTag

                }


            }
            is PowerTag.GameState.ScenarioID -> {
                game.scenarioID = powerTag.id.toInt()
            }
            is PowerTag.PowerTaskList.Block -> {
                powerTag.list.forEach {
                    handle(it)
                }


            }
            is PowerTag.PowerTaskList.CreateGame -> {
                //初始化卡牌
                onGameStarted()
//                gameEventListener(GameEvent.OnGameStarted)
            }
            is PowerTag.PowerTaskList.FullEntity -> {
//                handleFullEntity(tag)
                handleFullEntity(powerTag)
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
                powerTag.cardId?.apply {
                    entityIdAndCardIdMap[powerTag.entity.id] = this
                }
            }

            is PowerTag.PowerTaskList.ShowEntity -> {
                handleShowEntity(powerTag)
                entityIdAndCardIdMap[powerTag.entity.id] = powerTag.cardId
            }
            is PowerTag.PowerTaskList.TagChange -> {
                handleTagChange(powerTag)

            }
        }


//        tag.toString().log()

        (powerTag as? ZoneUpdatable)?.apply {


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
//                zoneChangedEventList.add(it)
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
                    if (cardId != null)
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
                    if (cardId != null)
                        insertCardToDeck(cardId)

                } else if (it.currentZone == Zone.Play && it.newZone == Zone.Graveyard) {
                    onGraveyardCardsChanged(cardId, it.isUser)
                } else if (it.newZone == Zone.Hand || it.currentZone == Zone.Hand) {
                    //手牌
                    if (!it.isUser) {
//                        handleOpponentHandChanged(it)

                    }
                }
            }
        }


    }

    private fun onGraveyardCardsChanged(cardId: String?, user: Boolean) {
        if (cardId == null) {
            return
        }
        _gameEventFlow.value = GameEvent.InsertCardToGraveyard(cardId, user)
    }


//    private fun findCardById(cardId: String) =
//        allCard.find { it.id == cardId } ?: throw RuntimeException("id为 $cardId 没有这张牌")


    private fun removeCardFromDeck(cardId: String) {
        _gameEventFlow.value = (GameEvent.RemoveCardFromUserDeck(cardId))
    }

    private fun insertCardToDeck(cardId: String) {
        _gameEventFlow.value = (GameEvent.InsertCardToUserDeck(cardId))
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

    private fun onGameOver() {
//        _deckLeftCardList.value = deckCardList
        game.endTime = System.currentTimeMillis()
        _gameEventFlow.value = GameEvent.OnGameOver(game)
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

    /**
     * 游戏开始
     */
    private fun onGameStarted() {
        "游戏开始了".log()
        game.startTime = System.currentTimeMillis()
        currentTurn = 0
        _gameEventFlow.value = (GameEvent.OnGameStart)

    }


    private fun handleFullEntity(fullEntity: PowerTag.PowerTaskList.FullEntity) {
        fullEntity.isUpdateHero()?.apply {

            val cardClass = allCard.find {
                it.id == second
            }?.cardClass ?: return

            "更新英雄 $this $cardClass".log()

            if (first == user?.id) {
                game.userHero = cardClass
            } else if (first == opponent?.id) {
                game.opponentHero = cardClass
            }
        }
    }
}

