package com.ke.hs_tracker.module.ui.deckbattledetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.db.Game
import com.ke.mvvm.base.data.successOr
import com.ke.mvvm.base.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BattleRecordsViewModel @Inject constructor(
    private val getGamesByDeckCodeAndNameUseCase: GetGamesByDeckCodeAndNameUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _games = MutableStateFlow<List<Game>>(emptyList())

    internal val games: StateFlow<List<Game>>
        get() = _games

    init {

        viewModelScope.launch {
            val code = savedStateHandle.get<String>(DeckBattleDetailActivity.EXTRA_DECK_CODE)!!
            val name = savedStateHandle.get<String>(DeckBattleDetailActivity.EXTRA_DECK_NAME)!!
            _games.value = getGamesByDeckCodeAndNameUseCase(code to name).successOr(emptyList())
        }
    }
}