package com.ke.hs_tracker.module.ui.classbattledetail

import com.ke.hs_tracker.module.entity.CardClass

internal data class ClassBattleItem(
    val hero: CardClass,
    val times: Int,
    val win: Int,
    val loss: Int,
    val rate: Int
)
