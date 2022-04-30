package com.ke.hs_tracker.module.entity

import com.ke.hs_tracker.module.db.ZonePositionChangedEvent


sealed interface PowerTag {


    sealed interface PowerTaskList : PowerTag {


        /**
         * 创建游戏
         */
        //D 19:55:18.1257030 GameState.DebugPrintPower() - CREATE_GAME
        //D 19:55:18.1257030 GameState.DebugPrintPower() -     GameEntity EntityID=1
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=CARDTYPE value=GAME
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=ZONE value=PLAY
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=ENTITY_ID value=1
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=937 value=2
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=SPAWN_TIME_COUNT value=1
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=GAME_SEED value=1950487951
        //D 19:55:18.1257030 GameState.DebugPrintPower() -     Player EntityID=2 PlayerID=1 GameAccountId=[hi=144115211015832391 lo=191215280]
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=CONTROLLER value=1
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=CARDTYPE value=PLAYER
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=PLAYER_ID value=1
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=HERO_ENTITY value=64
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=MAXHANDSIZE value=10
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=STARTHANDSIZE value=4
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=TEAM_ID value=1
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=ZONE value=PLAY
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=ENTITY_ID value=2
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=MAXRESOURCES value=10
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=SPAWN_TIME_COUNT value=1
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=AVRANK value=336
        //D 19:55:18.1257030 GameState.DebugPrintPower() -     Player EntityID=3 PlayerID=2 GameAccountId=[hi=144115211015832391 lo=44511141]
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=CONTROLLER value=2
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=CARDTYPE value=PLAYER
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=PLAYER_ID value=2
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=HERO_ENTITY value=66
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=MAXHANDSIZE value=10
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=STARTHANDSIZE value=4
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=TEAM_ID value=2
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=ZONE value=PLAY
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=ENTITY_ID value=3
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=MAXRESOURCES value=10
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=SPAWN_TIME_COUNT value=1
        //D 19:55:18.1257030 GameState.DebugPrintPower() -         tag=AVRANK value=338
        data class CreateGame(
            val gameEntity: GameEntity,
            val player1: Player,
            val player2: Player
        ) : PowerTaskList


        data class TagChange(
            override val entity: Entity,
            val tag: String,
            val value: String,
        ) : PowerTaskList, ZoneUpdatable {

            /**
             * 是否是游戏完成的标志
             */
            val isGameComplete: Boolean =
                entity.entityName == "GameEntity" && tag.equals("state", true) && value.equals(
                    "COMPLETE",
                    true
                )

            /**
             * 是否是玩家胜利或失败
             */
            val isPlayerWonOrLost: Pair<String, Boolean>?
                get() {
                    return if (tag == "PLAYSTATE" && value == "WON") {
                        entity.entityName to true
                    } else if (tag == "PLAYSTATE" && value == "LOST") {
                        entity.entityName to false
                    } else {
                        null
                    }
                }

            //TAG_CHANGE Entity=GameEntity tag=TURN value=1
            fun isTurnChanged(): Int? {
                if (entity.isGameEntity && tag == "TURN") {
                    return value.toIntOrNull()
                }
                return null
            }

            //TAG_CHANGE Entity=GameEntity tag=NUM_TURNS_IN_PLAY value=5
            fun isNumTurnsInPlayChanged(): Int? {
                if (entity.isGameEntity && tag == "NUM_TURNS_IN_PLAY") {
                    return value.toIntOrNull()
                }
                return null
            }

            override fun getZoneString(): String? {
                return if (tag == "ZONE") value else null
            }

            override fun getZonePositionString(): String? {
                return if (tag == "ZONE_POSITION") value else null
            }
        }


        data class FullEntity(
            override val entity: Entity,
            val payloads: MutableMap<String, String> = mutableMapOf(),
            val cardId: String?
        ) : PowerTaskList, ZoneUpdatable {
            fun append(value: Pair<String, String>) {
                payloads[value.first] = value.second
            }

            /**
             * 是否是更新英雄置于战场
             */
            fun isUpdateHero(): Pair<Int, String?>? {
                if (entity.zone == Zone.Play && payloads.count {
                        it.key == "CARDTYPE" && it.value == "HERO"
                    } == 1) {
                    return entity.player to entity.cardId
                }

                return null
            }

            //FULL_ENTITY - Updating [entityName=UNKNOWN ENTITY [cardType=INVALID] id=50 zone=DECK zonePos=0 cardId= player=2] CardID=
            //    tag=ZONE value=DECK
            //    tag=CONTROLLER value=2
            //    tag=ENTITY_ID value=50


            override fun getZoneString(): String? {
                return payloads["ZONE"]
            }

            override fun getZonePositionString(): String? {
                return payloads["ZONE_POSITION"]
            }

            override fun convertEntity(entity: Entity): Entity {
                entity.run {
                    return Entity(
                        entityName,
                        gameCardType,
                        id,
                        zone,
                        zonePosition,
                        this@FullEntity.cardId,
                        player
                    )
                }
            }

            //起始发牌
            //FULL_ENTITY - Updating [entityName=UNKNOWN ENTITY [cardType=INVALID] id=49 zone=DECK zonePos=0 cardId= player=2] CardID=
            //    tag=ZONE value=DECK
            //    tag=CONTROLLER value=2
            //    tag=ENTITY_ID value=49
            //发现一张牌
            //FULL_ENTITY - Updating [entityName=UNKNOWN ENTITY [cardType=INVALID] id=113 zone=SETASIDE zonePos=0 cardId= player=2] CardID=
            //    tag=ZONE value=SETASIDE
            //    tag=CONTROLLER value=2
            //    tag=ENTITY_ID value=113
//            override fun shouldIgnoreSameZone(): Boolean {
//                return true
//            }
        }

        data class ShowEntity(
            override val entity: Entity,
            val cardId: String,
            val payloads: MutableMap<String, String> = mutableMapOf()
        ) : PowerTaskList, ZoneUpdatable {
            fun append(value: Pair<String, String>) {
                payloads[value.first] = value.second
            }

            fun entityWithCardId(): Entity {
                return entity.run {
                    Entity(
                        entityName,
                        gameCardType,
                        id,
                        zone,
                        zonePosition,
                        this@ShowEntity.cardId,
                        player
                    )
                }
            }

            override fun getZoneString(): String? {
                return payloads["ZONE"]
            }

            override fun getZonePositionString(): String? {
                return payloads["ZONE_POSITION"]
            }

            override fun convertEntity(entity: Entity): Entity {
                entity.run {
                    return Entity(
                        entityName,
                        gameCardType,
                        id,
                        zone,
                        zonePosition,
                        this@ShowEntity.cardId,
                        player
                    )
                }
            }


//            override fun isUpdated(): Pair<Entity, Zone>? {
//                val newZoneString = payloads["ZONE"] ?: return null
//                val newZone = newZoneString.toZone(Zone.Unknown)
//                if (newZone == Zone.Unknown) {
//                    throw RuntimeException("错误的zone $newZoneString")
//                }
//                if (entity.zone == newZone) {
//                    return null
//                }
//                return entity to newZone
//            }
        }

        data class Block(
            val type: BlockType,
            val entity: Entity,
            val target: Entity?,
            val list: List<PowerTag>
        ) : PowerTaskList {

            /**
             * 是否是第一回合
             */
            fun ifFirstTurn(): Boolean {
                return type == BlockType.Trigger && entity.isGameEntity && list.mapNotNull {
                    it as? TagChange
                }.any {
                    it.tag == "FIRST_PLAYER" && it.value == "1"
                }

            }

            private fun isTurnChanged(): Int? {
                return list.mapNotNull {
                    it as? TagChange
                }.find {
                    it.isTurnChanged() != null
                }?.isTurnChanged()
            }

            private fun isNumTurnsInPlayChanged(): Int? {
                return list.mapNotNull {
                    it as? TagChange
                }.find {
                    it.isNumTurnsInPlayChanged() != null
                }?.isNumTurnsInPlayChanged()
            }


            private fun flush(): List<EntityWithPayload> {
                val entityWithPayloadList = mutableListOf<EntityWithPayload>()

                list.forEach {
                    when (it) {

                        is ShowEntity -> {
                            val entity = it.entityWithCardId()
                            val entityWithPayload = EntityWithPayload(entity)
//                            entityWithPayload.payload.addAll(it.payloads)
                            it.payloads.forEach { entry ->
                                entityWithPayload.add(entry.toPair())
                            }
                            entityWithPayloadList.add(entityWithPayload)

                        }
                        is TagChange -> {
                            findEntityFromList(it.entity.id, entityWithPayloadList)?.apply {
                                add(it.tag to it.value)
                            }
                        }
                        else -> {

                        }
                    }
                }

                return entityWithPayloadList
            }

            private fun findEntityFromList(
                entityId: Int,
                list: List<EntityWithPayload>
            ): EntityWithPayload? {
                return list.find {
                    it.entity.id == entityId
                }
            }


        }
    }

    sealed interface GameState : PowerTag {
        data class BuildNumber(val number: String) : GameState

        data class GameType(val type: String) : GameState

        data class FormatType(val type: String) : GameState

        data class ScenarioID(val id: String) : GameState

        data class PlayerMapping(val id: Int, val name: String) : GameState {

            /**
             * 是否是先手
             */
            val first: Boolean = id == 1

            /**
             * 是否是当前用户
             */
            val isUser: Boolean = name != "UNKNOWN HUMAN PLAYER"
        }
    }


}

interface ZoneUpdatable {
    /**
     * 是否更新了位置
     */
    fun isUpdateZone(userPlayerId: Int?): ZonePositionChangedEvent? {
        val newZoneString = getZoneString()
        val newZone = newZoneString?.toZone(Zone.Unknown)
        if (newZone == Zone.Unknown) {
            throw RuntimeException("错误的zone $newZoneString")
        }

        val position = getZonePositionString()?.toIntOrNull()

        if (newZone == null && position == null) {
            return null
        }

        val entity = convertEntity(entity)

        return ZonePositionChangedEvent(
            entityId = entity.id,
            cardId = entity.cardId,
            currentZone = entity.zone,
            isUser = entity.player == userPlayerId,
            currentPosition = entity.zonePosition,
            newZone = newZone ?: entity.zone,
            newPosition = position ?: entity.zonePosition
        )
    }

    fun getZoneString(): String?

    fun getZonePositionString(): String?

    val entity: Entity


    fun convertEntity(entity: Entity): Entity = entity


}

//interface ZonePositionUpdatable {
//    /**
//     * 是否更新了位置
//     */
//    fun isUpdatePosition(): Pair<Entity, Int>? {
//
//        val position = getZonePositionString()?.toIntOrNull() ?: return null
//
//        return entity to position
//    }
//
//    fun getZonePositionString(): String?
//
//    val entity: Entity
//
//
//}

