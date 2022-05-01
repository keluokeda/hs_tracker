package com.ke.hs_tracker.module.ui.summary

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RateByDeckFragment : BattleRateListFragment() {

    override val viewModel: DeckBattleRateListViewModel by viewModels()
}