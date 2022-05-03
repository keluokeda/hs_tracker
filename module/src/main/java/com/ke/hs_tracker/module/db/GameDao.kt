package com.ke.hs_tracker.module.db

import androidx.room.*
import com.ke.hs_tracker.module.entity.CardClass

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game)


    /**
     * 插入所有
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(games: List<Game>)

    @Update
    suspend fun update(game: Game)

    /**
     * 删除全部
     */
    @Query("delete from game")
    suspend fun deleteAll()

    /**
     * 查询所有
     */
    @Query("select * from game")
    suspend fun getAll(): List<Game>

    /**
     * 查找用户某个英雄的总对局
     */
    @Query("select * from game where user_hero = :cardClass")
    suspend fun getByHero(cardClass: CardClass): List<Game>

    /**
     * 获取总的对局数
     */
    @Query("select count(*) from game")
    suspend fun getGameCount(): Int

    /**
     * 获取玩家胜率对局数
     */
    @Query("select count(*) from game where is_user_win = 1")
    suspend fun getUserWinCount(): Int
}