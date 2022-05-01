package com.ke.hs_tracker.module.ui.summary

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RateByHeroFragment : BattleRateListFragment() {
    override val viewModel: HeroBattleRateListViewModel by viewModels()
}