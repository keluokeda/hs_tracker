package com.ke.hs_tracker.module.ui.summary

import androidx.annotation.IntRange
import com.ke.hs_tracker.module.entity.CardClass

/**
 * 对局胜率
 */
sealed interface BattleRateItem {
    /**
     * 胜率局数
     */
    val winCount: Int

    /**
     * 失败局数
     */
    val lostCount: Int

    /**
     * 总局数
     */
    val allCount: Int

    /**
     * 先手局数
     */
    val firstHandCount: Int

    /**
     * 胜率
     */
    @get:IntRange(from = 0, to = 100)
    val rate: Int

    /**
     * 职业对战胜率
     */
    data class ClassBattleRate(
        override val winCount: Int,
        override val lostCount: Int,
        override val firstHandCount: Int,
        val cardClass: CardClass
    ) : BattleRateItem {
        override val allCount: Int
            get() = winCount + lostCount

        override val rate: Int
            get() = if (allCount == 0) 0 else winCount * 100 / allCount
    }

    /**
     * 卡组对战胜率
     */
    data class DeckBattleRate(
        override val winCount: Int,
        override val lostCount: Int,
        override val firstHandCount: Int,
        val deckName: String,
        val deckCode: String
    ) : BattleRateItem {

        override val allCount: Int
            get() = winCount + lostCount

        override val rate: Int
            get() = winCount * 100 / allCount
    }
}