package com.ke.hs_tracker.module.entity

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * 随从种族
 */
enum class Race {
    /**
     * 海盗
     */
    Pirate,

    /**
     * 机械
     */
    Mechanical,

    /**
     * 龙
     */
    Dragon,

    /**
     * 野兽
     */
    Beast,

    /**
     * 鱼人
     */
    Murloc,

    /**
     * 恶魔
     */
    Demon,

    /**
     * 图腾
     */
    Totem,

    /**
     * 元素
     */
    Elemental,

    /**
     * 全部
     */
    All,

    /**
     * 野猪人
     */
    Quilboar,

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