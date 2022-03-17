package com.ke.hs_tracker.module.entity

import androidx.annotation.StringRes
import com.ke.hs_tracker.module.R
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * 法术类型
 */
enum class SpellSchool(@StringRes val titleRes: Int) {
    /**
     * 奥数
     */
    Arcane(R.string.module_arcane),

//    /**
//     * 冰霜
//     */
//    Freeze,

    /**
     * 冰霜
     */
    Frost(R.string.module_frost),

    /**
     * 火焰
     */
    Fire(R.string.module_fire),

    /**
     * 自然
     */
    Nature(R.string.module_nature),

    /**
     * 暗影
     */
    Shadow(R.string.module_shadow),

    /**
     * 神圣
     */
    Holy(R.string.module_holy),

    /**
     * 邪能
     */
    Fel(R.string.module_Fel),

    /**
     * 冲锋攻击
     */
    PhysicalCombat(R.string.module_all)
}

class SpellSchoolAdapter {
    @FromJson
    fun fromJson(value: String): SpellSchool? {

        return SpellSchool.values().find { it.name.equals(value.replace("_", ""), true) }

    }

    @ToJson
    fun toJson(spellSchool: SpellSchool?) = spellSchool?.name?.uppercase()
}