package com.ke.hs_tracker.module.db

import androidx.room.*
import com.ke.hs_tracker.module.entity.CardClass

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

    /**
     * 查找用户某个英雄的总对局
     */
    @Query("select * from game where user_hero = :cardClass")
    suspend fun getByHero(cardClass: CardClass): List<Game>
}