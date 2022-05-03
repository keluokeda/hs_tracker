package com.ke.hs_tracker.module.ui.chart

import androidx.annotation.ColorRes

internal data class PieChartData(
    val label: String,
    val value: Int,
    @ColorRes
    val color: Int
)
