package com.ke.hs_tracker.module.ui.chart

import com.ke.hs_tracker.module.entity.CardClass

internal data class SummaryChartViewData(
    /**
     * 总胜利次数
     */
    val winCount: Int,
    /**
     * 总失败次数
     */
    val lossCount: Int,
    /**
     * 先手次数
     */
    val firstHandCount: Int,
    /**
     * 后手次数
     */
    val secondHandCount: Int,
    /**
     * 先手胜利次数
     */
    val firstHandWinCount: Int,
    /**
     * 先手失败次数
     */
    val firstHandLossCount: Int,
    /**
     * 后手胜利次数
     */
    val secondHandWinCount: Int,
    /**
     * 后手失败次数
     */
    val secondHandLossCount: Int,
    /**
     * 职业出现频度
     */
    val classCounts: List<Pair<CardClass, Int>>
)
