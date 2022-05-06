package com.ke.hs_tracker.module.ui.deckbattledetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.domain.ParseDeckCodeUseCase
import com.ke.hs_tracker.module.entity.CardBean
import com.ke.mvvm.base.data.successOr
import com.ke.mvvm.base.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    private val parseDeckCodeUseCase: ParseDeckCodeUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _cardList = MutableStateFlow(emptyList<CardBean>())
    internal val cardList: StateFlow<List<CardBean>>
        get() = _cardList

    init {
        viewModelScope.launch {
            val code = savedStateHandle.get<String>(DeckBattleDetailActivity.EXTRA_DECK_CODE)!!

            _cardList.value = parseDeckCodeUseCase(code).successOr(emptyList())
        }
    }
}