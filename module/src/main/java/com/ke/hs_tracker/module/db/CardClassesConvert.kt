package com.ke.hs_tracker.module.db

import androidx.room.TypeConverter
import com.ke.hs_tracker.module.entity.CardClass

class CardClassesConvert {

    @TypeConverter
    fun longToClasses(value: Long?): List<CardClass> {
        if (value == null) {
            return emptyList()
        }
        return CardClass.values()
            .map {
                it to (1L shl it.ordinal)
            }
            .filter {
                it.second and value == it.second
            }
            .map { it.first }
    }

    @TypeConverter
    fun classesToLong(list: List<CardClass>): Long {
        if (list.isEmpty()) {
            return 0
        }
        var result = 0L
        list.map {
            1L shl it.ordinal
        }.forEach {
            result = result or it
        }

        return result
    }
}