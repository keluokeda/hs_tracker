package com.ke.hs_tracker.module.ui.migrate

import androidx.annotation.WorkerThread
import com.ke.hs_tracker.module.db.*
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import javax.inject.Inject

internal class MigrateDataConvert @Inject constructor(
    private val gameDao: GameDao,
    private val zonePositionChangedEventDao: ZonePositionChangedEventDao,
    private val moshi: Moshi
) {

    private val adapter = moshi.adapter(MigrateData::class.java)

    @WorkerThread
    suspend fun getJsonString(): String {

        val migrateData =
            MigrateData(DATABASE_VERSION, gameDao.getAll(), zonePositionChangedEventDao.getAll())

        return adapter.toJson(migrateData)

    }

    suspend fun save(jsonString: String): Boolean {
        val data = adapter.fromJson(jsonString) ?: return false
        saveData(data)
        return true
    }

    @WorkerThread
    private suspend fun saveData(migrateData: MigrateData) {
        gameDao.insert(migrateData.games)
        zonePositionChangedEventDao.insertAll(migrateData.zonePositionChangedEvents)
    }
}

@JsonClass(generateAdapter = true)
internal data class MigrateData(
    val version: Int,
    val games: List<Game>,
    val zonePositionChangedEvents: List<ZonePositionChangedEvent>
)