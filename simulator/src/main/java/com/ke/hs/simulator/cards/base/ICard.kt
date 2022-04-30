package com.ke.hs.simulator.cards.base

import androidx.annotation.StringRes
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.hs_tracker.module.entity.Rarity

interface ICard {

    val id: String

    val dbfId: Int

    /**
     * 费用
     */
    var cost: Int

    /**
     * 职业
     */
    val cardClass: CardClass

    /**
     * 名称
     */
    val name: Int

    /**
     * 品质 衍生牌没有
     */
    val rarity: Rarity?
}