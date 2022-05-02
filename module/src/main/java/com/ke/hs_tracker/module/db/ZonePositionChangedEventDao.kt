package com.ke.hs_tracker.module.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ZonePositionChangedEventDao {

    @Insert
    suspend fun insertAll(list: List<ZonePositionChangedEvent>)

    @Query("select * from zone_position_updated_event where game_id = :gameId")
    suspend fun getAllByGameId(gameId: String): List<ZonePositionChangedEvent>

    /**
     * 获取所有
     */
    @Query("select * from zone_position_updated_event")
    suspend fun getAll(): List<ZonePositionChangedEvent>
}