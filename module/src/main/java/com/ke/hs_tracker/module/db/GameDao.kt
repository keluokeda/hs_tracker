package com.ke.hs_tracker.module.db

import androidx.room.*

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game)


    @Update
    suspend fun update(game: Game)

    @Query("delete from game")
    suspend fun deleteAll()

    @Query("select * from game")
    suspend fun getAll(): List<Game>
}