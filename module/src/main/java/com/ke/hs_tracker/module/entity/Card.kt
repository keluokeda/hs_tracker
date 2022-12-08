package com.ke.hs_tracker.module.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Entity(tableName = "card")
@JsonClass(generateAdapter = true)
@Parcelize
data class Card(
    /**
     * 画家
     */
    val artist: String? = null,
    /**
     * 名称
     */
    val name: String,
    /**
     * 费用
     */
    val cost: Int = 0,
    @PrimaryKey
    val id: String,
    val dbfId: Int,
    val text: String = "",
    //属于哪个版本 例如 TGT
    val set: String = "",
    val type: CardType = CardType.None,
    val cardClass: CardClass? = null,
    val classes: List<CardClass> = emptyList(),
    val mechanics: List<Mechanics> = emptyList(),
    /**
     * 个性介绍
     */
    val flavor: String = "",

    val rarity: Rarity? = null,

    /**
     * 武器耐久
     */
    val durability: Int = 0,
    /**
     * 英雄牌的护甲
     */
    val armor: Int = 0,

    val collectible: Boolean = false,
    /**
     * 法术类型
     */
    val spellSchool: SpellSchool? = null,

    /**
     * 随从种族
     */
    val race: Race? = null,

    /**
     * 随从攻击力
     */
    val attack: Int = 0,
    /**
     * 随从生命值
     */
    val health: Int = 0,
) : Parcelable

