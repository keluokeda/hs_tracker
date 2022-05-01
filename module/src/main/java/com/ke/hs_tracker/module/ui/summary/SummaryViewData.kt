package com.ke.hs_tracker.module.ui.summary

import androidx.annotation.IntRange

internal data class SummaryViewData(
    val winCount: Int = 0,
    val lostCount: Int = 0,
    /**
     * 胜率
     */
    @IntRange(from = 0, to = 100)
    val rate: Int = 100,

)


