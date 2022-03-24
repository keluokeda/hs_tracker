package com.ke.hs_tracker.module.entity

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ke.hs_tracker.module.R
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

enum class CardClass(
    @StringRes
    val titleRes: Int,
    @ColorRes
    val color: Int = 0,

    @DrawableRes
    val roundIcon: Int? = null
) {
    /**
     * 法师
     */
    Mage(R.string.module_mage, R.color.module_mage, R.drawable.module_image_round_mage),

    /**
     * 术士
     */
    Warlock(R.string.module_warlock, R.color.module_warlock, R.drawable.module_image_round_warlock),

    /**
     * 牧师
     */
    Priest(R.string.module_priest, R.color.module_priest, R.drawable.module_image_round_priest),

    /**
     * 德鲁伊
     */
    Druid(R.string.module_druid, R.color.module_druid, R.drawable.module_image_round_druid),

    /**
     * 盗贼
     */
    Rogue(R.string.module_rogue, R.color.module_rogue, R.drawable.module_image_round_rogue),

    /**
     * 萨满
     */
    Shaman(R.string.module_shaman, R.color.module_shaman, R.drawable.module_image_round_shaman),


    /**
     * 猎人
     */
    Hunter(R.string.module_hunter, R.color.module_hunter, R.drawable.module_image_round_hunter),

    /**
     * 圣骑士
     */
    Paladin(R.string.module_paladin, R.color.module_paladin, R.drawable.module_image_round_paladin),

    /**
     * 战士
     */
    Warrior(R.string.module_warrior, R.color.module_warrior, R.drawable.module_image_round_warrior),

    /**
     * 恶魔猎手
     */
    DemonHunter(
        R.string.module_demon_hunter,
        R.color.module_demon_hunter,
        R.drawable.module_image_round_demon_hunter
    ),

    /**
     * 中立
     */
    Neutral(R.string.module_neutral, R.color.module_neutral),

    /**
     * 威兹班
     */
    Whizbang(R.string.module_whizbang, 0),

    /**
     * 梦境牌
     */
    Dream(R.string.module_dream, 0),

    /**
     * 死亡骑士
     */
    DeathKnight(R.string.module_death_knight, R.color.module_death_knight),
}

class CardClassAdapter {
    @FromJson
    fun fromJson(value: String): CardClass {

        return EnumMoshiAdapter.fromJson(value, CardClass.values())

    }

    @ToJson
    fun toJson(cardClass: CardClass) = cardClass.name.uppercase()
}