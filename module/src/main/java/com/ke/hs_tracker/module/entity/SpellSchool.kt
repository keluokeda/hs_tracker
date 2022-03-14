package com.ke.hs_tracker.module.entity

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * 法术类型
 */
enum class SpellSchool {
    /**
     * 奥数
     */
    Arcane,

    /**
     * 冰霜
     */
    Freeze,

    /**
     * 冻结
     */
    Frost,

    /**
     * 火焰
     */
    Fire,

    /**
     * 自然
     */
    Nature,

    /**
     * 暗影
     */
    Shadow,

    /**
     * 神圣
     */
    Holy,

    /**
     * 邪能
     */
    Fel,

    /**
     * 冲锋攻击
     */
    PhysicalCombat
}

class SpellSchoolAdapter {
    @FromJson
    fun fromJson(value: String): SpellSchool {

        return EnumMoshiAdapter.fromJson(value, SpellSchool.values())
    }

    @ToJson
    fun toJson(spellSchool: SpellSchool) = spellSchool.name.uppercase()
}