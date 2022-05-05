package com.ke.hs_tracker.module.ui.classbattledetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.mvvm.base.data.successOr
import com.ke.mvvm.base.ui.BaseContentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ClassBattleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getClassBattleItemListUseCase: GetClassBattleItemListUseCase
) :
    BaseContentViewModel<List<ClassBattleItem>>() {

    internal val cardClass: CardClass =
        savedStateHandle.get<CardClass>(ClassBattleDetailActivity.EXTRA_CARD_CLASS)!!

    init {
        viewModelScope.launch {
            showLoading()
            showContent(getClassBattleItemListUseCase(cardClass).successOr(emptyList()))
        }
    }

}