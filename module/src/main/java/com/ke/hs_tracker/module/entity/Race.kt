package com.ke.hs_tracker.module.entity

import androidx.annotation.StringRes
import com.ke.hs_tracker.module.R
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * 随从种族
 */
enum class Race(@StringRes val titleRes: Int? = null) {
    /**
     * 海盗
     */
    Pirate(R.string.module_pirate),

    /**
     * 机械
     */
    Mechanical(R.string.module_mechanical),

    /**
     * 龙
     */
    Dragon(R.string.module_dragon),

    /**
     * 野兽
     */
    Beast(R.string.module_beast),

    /**
     * 鱼人
     */
    Murloc(R.string.module_murloc),

    /**
     * 恶魔
     */
    Demon(R.string.module_demon),

    /**
     * 图腾
     */
    Totem(R.string.module_totem),

    /**
     * 元素
     */
    Elemental(R.string.module_elemental),


    Naga(R.string.module_naga),


    /**
     * 野猪人
     */
    Quilboar(R.string.module_quilboar),

    /**
     * 全部
     */
    All(R.string.module_all),

    /**
     * 佣兵
     */
    ORC,

    Troll,

    /**
     * 暗夜精灵
     */
    NightElf,

    /**
     * 食人魔
     */
    Ogre,

    /**
     * 牛头人
     */
    Tauren,

    Lock,

    Human,

    Bloodelf,

    /**
     * 亡灵
     */
    Undead,

    Gnome,

    Dwarf,

    /**
     * 德莱尼
     */
    Draenei,

    Halforc,

    Centaur,


    Goblin,

    Furbolg,

    Egg,

    Worgen,

    Treant,

    Highelf,
}

class RaceAdapter {


    @FromJson
    fun fromJson(value: String): Race {
        return EnumMoshiAdapter.fromJson(value, Race.values())
    }

    @ToJson
    fun toJson(value: Race) = EnumMoshiAdapter.toJson(value)
}