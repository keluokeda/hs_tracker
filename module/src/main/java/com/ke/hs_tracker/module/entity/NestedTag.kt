package com.ke.hs_tracker.module.entity

internal sealed interface NestedTag {
    object CreateGame : NestedTag
    data class GameEntity(val id: Int) : NestedTag
    data class Tag(val key: String, val value: String) : NestedTag {
        fun toPair(): Pair<String, String> = key to value
    }

    data class Player(val entityId: Int, val playerId: Int) : NestedTag

    //FULL_ENTITY - Updating [entityName=UNKNOWN ENTITY [cardType=INVALID] id=63 zone=DECK zonePos=0 cardId= player=2] CardID=
    //FULL_ENTITY - Updating [entityName=全副武装！ id=65 zone=PLAY zonePos=0 cardId=HERO_01bp player=1] CardID=HERO_01bp
    data class FullEntity(
        val entity: Entity
    ) : NestedTag

    data class Block(
        val blockType: BlockType,
        val entity: Entity,
        val target: Entity?
    ) : NestedTag


    data class TagChange(
        val entity: Entity,
        val tag: String,
        val value: String
    ) : NestedTag {
        fun convert(): PowerTag.PowerTaskList.TagChange {
            return PowerTag.PowerTaskList.TagChange(entity, tag, value)
        }
    }


    data class ShowEntity(
        val entity: Entity,
        val cardId: String
    ) : NestedTag

    object BlockEnd : NestedTag
}

//internal fun NestedTag.FullEntity.toUpdating(): PowerTag.PowerTaskList.FullEntity.Updating {
//    return PowerTag.PowerTaskList.FullEntity.Updating(
//        entityName, cardType, id, zone, zonePosition, cardId, player
//    )
//}