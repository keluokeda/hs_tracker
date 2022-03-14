package com.ke.hs_tracker.module.entity

import com.ke.hs_tracker.module.parser.PowerParserImpl


//有三种形式
//1,Entity=[entityName=腐食研习 id=29 zone=PLAY zonePos=0 cardId=SCH_300 player=1]
//2,Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=29 zone=HAND zonePos=3 cardId= player=1]
//3,Entity=失落的裤子#5629
data class Entity(
    val entityName: String,
    val gameCardType: GameCardType? = null,
    val id: Int = -1,
    val zone: Zone = Zone.Play,
    val zonePosition: Int = -1,
    val cardId: String? = null,
    val player: Int = -1
) {

    val entityType: EntityType
        get() = when {
            gameCardType == null && id == -1 && zone == Zone.Play && zonePosition == -1 && cardId == null && player == -1 -> EntityType.Name
            gameCardType == GameCardType.Invalid -> EntityType.Invalid
            else -> EntityType.Clear
        }


    companion object {
        internal fun createFromContent(content: String): Entity? {

            if (content == "0") {
                return null
            }


            var matchResult = PowerParserImpl.FULL_ENTITY_CONTENT1_PATTERN.matchEntire(content)
            if (matchResult != null) {
                return Entity(
                    matchResult.groupValues[1],
                    matchResult.groupValues[2].toCardType(GameCardType.Invalid),
                    matchResult.groupValues[3].toIntOrNull() ?: 0,
                    matchResult.groupValues[4].toZone(),
                    matchResult.groupValues[5].toIntOrNull() ?: 0,
                    matchResult.groupValues[6].ifBlank { null },
                    matchResult.groupValues[7].toIntOrNull() ?: 0
                )

            }
            matchResult = PowerParserImpl.FULL_ENTITY_CONTENT2_PATTERN.matchEntire(content)
                ?: return Entity(content)

            return Entity(
                matchResult.groupValues[1],
                GameCardType.Invalid,
                matchResult.groupValues[2].toIntOrNull() ?: 0,
                matchResult.groupValues[3].toZone(),
                matchResult.groupValues[4].toIntOrNull() ?: 0,
                matchResult.groupValues[5].ifBlank { null },
                matchResult.groupValues[6].toIntOrNull() ?: 0
            )

        }
    }
}

enum class EntityType {

    //Entity=失落的裤子#5629
    Name,

    //Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=29 zone=HAND zonePos=3 cardId= player=1]
    Invalid,

    //Entity=[entityName=腐食研习 id=29 zone=PLAY zonePos=0 cardId=SCH_300 player=1]
    Clear
}


data class GameEntity(
    val gameCardType: GameCardType,
    val entityId: Int
)

/**
 * 玩家
 */
data class Player(
    val entityId: Int,
    val playerId: Int,
    val controller: Int,
    val gameCardType: GameCardType,
    val heroEntity: Int,
    /**
     * 手牌上限
     */
    val maxHandSize: Int,
    /**
     * 起始手牌
     */
    val startHandSize: Int,
    val teamId: Int,
    /**
     * 费用上限 一般为10
     */
    val maxResources: Int
) {

    companion object {
        internal fun fromMap(map: Map<String, String>): Player {

            return Player(
                entityId = map["entityid"]?.toIntOrNull() ?: 0,
                playerId = map["playerid"]?.toIntOrNull() ?: 0,
                controller = map["controller"]?.toIntOrNull() ?: 0,
                gameCardType = (map["cardtype"] ?: "").toCardType(),
                heroEntity = map["heroentity"]?.toIntOrNull() ?: 0,
                maxHandSize = map["maxhandsize"]?.toIntOrNull() ?: 0,
                startHandSize = map["starthandsize"]?.toIntOrNull() ?: 0,
                teamId = map["teamid"]?.toIntOrNull() ?: 0,
                maxResources = map["maxresources"]?.toIntOrNull() ?: 0
            )

        }
    }
}
