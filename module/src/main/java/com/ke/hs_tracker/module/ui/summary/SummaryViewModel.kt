package com.ke.hs_tracker.module.ui.summary

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.ke.mvvm.base.data.successOr
import com.ke.mvvm.base.ui.BaseContentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SummaryViewModel @Inject constructor(
    private val getSummaryViewDataUseCase: GetSummaryViewDataUseCase
) : BaseContentViewModel<SummaryViewData>(), DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        refresh()
    }

    internal fun refresh() {
        viewModelScope.launch {
            showLoading()
            showContent(getSummaryViewDataUseCase(Unit).successOr(SummaryViewData()))
        }
    }

}