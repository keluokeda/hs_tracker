package com.ke.hs_tracker.module.ui.summary

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeckBattleRateListViewModel @Inject constructor(override val getBattleRateListUseCase: GetDeckBattleRateListUseCase) :
    BattleRateListViewModel<BattleRateItem.DeckBattleRate>()