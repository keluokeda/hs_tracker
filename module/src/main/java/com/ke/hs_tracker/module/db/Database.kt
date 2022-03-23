package com.ke.hs_tracker.module.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ke.hs_tracker.module.entity.Card

@Database(
    entities = [Card::class, Game::class],
    version = 2,
    exportSchema = false,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
@TypeConverters(
    CardClassesConvert::class, MechanicsListConvert::class
)
abstract class Database : RoomDatabase() {
    abstract fun cardDao(): CardDao

    abstract fun gameDao(): GameDao
}