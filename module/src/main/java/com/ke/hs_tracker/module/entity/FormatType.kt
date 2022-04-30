package com.ke.hs_tracker.module.entity

import androidx.annotation.StringRes
import com.ke.hs_tracker.module.R

enum class FormatType(@StringRes val title: Int) {
    Unknown(R.string.module_unknown),

    /**
     * 标准
     */
    Standard(R.string.module_standard),

    /**
     * 狂野
     */
    Wild(R.string.module_wild),

    /**
     * 经典
     */
    Classic(R.string.module_classic)
}

val String.toFormatType: FormatType
    get() = FormatType.values().find {
        this.contains(it.name, true)
    } ?: FormatType.Unknown