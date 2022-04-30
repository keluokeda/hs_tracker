package com.ke.hs_tracker.module.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ke.hs_tracker.module.entity.Card

@Database(
    entities = [Card::class, Game::class, ZonePositionChangedEvent::class],
    version = 3,
    exportSchema = true,

)
@TypeConverters(
    CardClassesConvert::class, MechanicsListConvert::class
)
abstract class Database : RoomDatabase() {
    abstract fun cardDao(): CardDao

    abstract fun gameDao(): GameDao


    abstract fun zonePositionChangedEventDao(): ZonePositionChangedEventDao
}