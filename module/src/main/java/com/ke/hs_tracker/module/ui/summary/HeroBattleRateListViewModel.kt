package com.ke.hs_tracker.module.ui.summary

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HeroBattleRateListViewModel @Inject constructor(override val getBattleRateListUseCase: GetHeroBattleRateListUseCase) :
    BattleRateListViewModel<BattleRateItem.ClassBattleRate>()