package com.ke.hs_tracker.module.ui.summary

import androidx.annotation.IntRange
import com.ke.hs_tracker.module.entity.CardClass

internal data class SummaryViewData(
    val winCount: Int = 0,
    val lostCount: Int = 0,
    /**
     * 胜率
     */
    @IntRange(from = 0, to = 100)
    val rate: Int = 100,

    val list: List<HeroBattleItem> = emptyList()
)

internal data class HeroBattleItem(
    val hero: CardClass,
    val winCount: Int,
    val lostCount: Int,
    /**
     * 胜率
     */
    @IntRange(from = 0, to = 100)
    val rate: Int
)
