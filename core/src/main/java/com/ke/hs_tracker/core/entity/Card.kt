package com.ke.hs_tracker.core.entity

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
data class Card(
    val name: String,
    val cost: Int = 0,
    val id: String,
    val dbfId: Int,
    val text: String = "",
    //属于哪个版本 例如 TGT
    val set: String,
    val type: CardType = CardType.None,
    val cardClass: CardClass,
    val classes: List<CardClass> = emptyList(),
    val flavor: String,
    val attach: Int = 0,
    val health: Int = 0
)

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
