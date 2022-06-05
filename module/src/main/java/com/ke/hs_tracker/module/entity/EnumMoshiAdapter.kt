package com.ke.hs_tracker.module.entity

object EnumMoshiAdapter {


    fun <T : Enum<T>> fromJson(value: String, enumList: Array<T>, fallback: T? = null): T {
        val enum = enumList
            .find { it.name.equals(value.replace("_", ""), true) } ?: fallback

        if (enum == null) {
            throw IllegalArgumentException("错误的 $value 类型")
        }

        return enum

    }

    fun <T : Enum<T>> toJson(value: T) = value.name.uppercase()
}