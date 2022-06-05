package com.ke.hs_tracker.module.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ke.hs_tracker.module.entity.Zone
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
@Entity(tableName = "zone_position_updated_event")
data class ZonePositionChangedEvent(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "game_id")
    var gameId: String = "",
    @ColumnInfo(name = "entity_id")
    val entityId: Int,
    @ColumnInfo(name = "card_id")
    var cardId: String?,
    @ColumnInfo(name = "card_name")
    var cardName: String? = null,
    @ColumnInfo(name = "is_user")
    var isUser: Boolean = false,
    @ColumnInfo(name = "current_zone")
    var currentZone: Zone,
    @ColumnInfo(name = "new_zone")
    var newZone: Zone,
    @ColumnInfo(name = "current_position")
    val currentPosition: Int,
    @ColumnInfo(name = "new_position")
    val newPosition: Int
) {

    //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=63 zone=DECK zonePos=0 cardId= player=2] tag=ZONE value=HAND
    //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=63 zone=DECK zonePos=0 cardId= player=2] tag=ZONE_POSITION value=1
    fun plus(event: ZonePositionChangedEvent): ZonePositionChangedEvent {
        if (event.entityId != entityId) {
            throw RuntimeException("两个要进行加的 entity id 不一致 $event")
        }

        if (currentZone == Zone.Deck && newZone == Zone.Deck && newPosition != 0) {
            //星界导致插入一张卡牌到事件中
            currentZone = event.newZone
            newZone = event.newZone
        }


        if (currentZone != event.currentZone) {
            throw RuntimeException("两个要进行加的 zone 不一致  $event")
        }

        val newPosAndZone =
            if (this.currentZone == event.newZone) event.newPosition to newZone else newPosition to event.newZone

        return ZonePositionChangedEvent(
            id,
            gameId,
            entityId,
            cardId,
            cardName,
            isUser,
            currentZone,
            newPosAndZone.second,
            currentPosition,
            newPosAndZone.first
        )
    }

    fun plusPlus(
        second: ZonePositionChangedEvent,
        third: ZonePositionChangedEvent
    ): ZonePositionChangedEvent {
        val oldZone = currentZone
        val list = listOf(this, second, third)
        val newZone = list.map {
            it.newZone
        }.firstOrNull {
            it != oldZone
        }

        val newPosition = third.newPosition

        return ZonePositionChangedEvent(
            id,
            gameId,
            entityId,
            cardId,
            cardName,
            isUser,
            currentZone,
            newZone ?: this.newZone,
            currentPosition,
            newPosition
        )
    }
}

//public operator fun ZonePositionChangedEvent.plus(event: ZonePositionChangedEvent): ZonePositionChangedEvent {
//    if (entityId != event.entityId) {
//        throw RuntimeException("不支持不同 entityId 的 event 相加")
//    }
//    if (this == event) {
//        return this
//    }
//}