package com.ke.hs_tracker.module.ui.deckbattledetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.mvvm.base.data.successOr
import com.ke.mvvm.base.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val getGamesByDeckCodeAndNameUseCase: GetGamesByDeckCodeAndNameUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val _heroBattleItems = MutableStateFlow<List<Pair<CardClass, Int>>>(emptyList())

    internal val heroBattleItems: StateFlow<List<Pair<CardClass, Int>>>
        get() = _heroBattleItems

    init {

        viewModelScope.launch {
            val code = savedStateHandle.get<String>(DeckBattleDetailActivity.EXTRA_DECK_CODE)!!
            val name = savedStateHandle.get<String>(DeckBattleDetailActivity.EXTRA_DECK_NAME)!!
            val list = getGamesByDeckCodeAndNameUseCase(code to name).successOr(emptyList())
            _heroBattleItems.value = list.groupBy {
                it.opponentHero!!
            }.map {
                it.key to it.value.count()
            }
        }
    }
}