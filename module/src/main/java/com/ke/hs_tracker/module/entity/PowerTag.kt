package com.ke.hs_tracker.module.entity


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
            val entity: Entity,
            val tag: String,
            val value: String,
        ) : PowerTaskList {

            /**
             * 是否是游戏完成的标志
             */
            val isGameComplete: Boolean =
                entity.entityName == "GameEntity" && tag.equals("state", true) && value.equals(
                    "COMPLETE",
                    true
                )
        }


        data class FullEntity(
            val entity: Entity,
            val payloads: MutableMap<String, String> = mutableMapOf()
        ) : PowerTaskList {
            fun append(value: Pair<String, String>) {
                payloads[value.first] = value.second
            }
        }

        data class ShowEntity(
            val entity: Entity,
            val cardId: String,
            val payloads: MutableMap<String, String> = mutableMapOf()
        ) : PowerTaskList {
            fun append(value: Pair<String, String>) {
                payloads[value.first] = value.second
            }
        }

        data class Block(
            val type: BlockType,
            val entity: Entity,
            val target: Entity?,
            val list: List<PowerTag>
        ) : PowerTaskList
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

