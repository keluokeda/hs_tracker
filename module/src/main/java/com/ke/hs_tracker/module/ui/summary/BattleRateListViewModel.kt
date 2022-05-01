package com.ke.hs_tracker.module.ui.summary

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.ke.mvvm.base.data.successOr
import com.ke.mvvm.base.domian.UseCase
import com.ke.mvvm.base.ui.BaseContentViewModel
import kotlinx.coroutines.launch

abstract class BattleRateListViewModel<T : BattleRateItem> :
    BaseContentViewModel<List<BattleRateItem>>(), DefaultLifecycleObserver {

    protected abstract val getBattleRateListUseCase: UseCase<Unit, List<T>>

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        refresh()
    }

    internal fun refresh() {
        viewModelScope.launch {
            showLoading()
            showContent(getBattleRateListUseCase(Unit).successOr(emptyList()))
        }
    }
}