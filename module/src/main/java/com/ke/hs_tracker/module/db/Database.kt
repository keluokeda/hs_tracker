package com.ke.hs_tracker.module.db

import androidx.room.*
import androidx.room.Database
import androidx.room.migration.AutoMigrationSpec
import com.ke.hs_tracker.module.entity.Card


const val DATABASE_VERSION = 4

@Database(
    entities = [Card::class, Game::class, ZonePositionChangedEvent::class],
    version = DATABASE_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 3,
            to = 4,
            spec = com.ke.hs_tracker.module.db.Database.RenameAttachToAttackMigration::class
        )
    ]

)
@TypeConverters(
    CardClassesConvert::class, MechanicsListConvert::class
)
abstract class Database : RoomDatabase() {

    @RenameColumn(tableName = "card", fromColumnName = "attach", toColumnName = "attack")
    class RenameAttachToAttackMigration : AutoMigrationSpec


    abstract fun cardDao(): CardDao

    abstract fun gameDao(): GameDao


    abstract fun zonePositionChangedEventDao(): ZonePositionChangedEventDao

    companion object {
        const val VERSION = 3
    }
}

