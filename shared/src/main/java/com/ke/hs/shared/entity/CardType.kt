package com.ke.hs.shared.entity

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson


enum class CardType {


    /**
     * 英雄
     */
    Hero,

    /**
     * 英雄技能
     */
    HeroPower,


    /**
     *衍生牌
     */
    Enchantment,

    /**
     * 法术
     */
    Spell,

    /**
     * 随从
     */
    Minion,

    /**
     * 武器
     */
    Weapon,


    None
}

class CardTypeAdapter {

    @FromJson
    fun fromJson(value: String): CardType {

        return CardType.values()
            .find { it.name.equals(value.replace("_", ""), true) } ?: CardType.None

    }

    @ToJson
    fun toJson(cardType: CardType) = cardType.name.uppercase()
}