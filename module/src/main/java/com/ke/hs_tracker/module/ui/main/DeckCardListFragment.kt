package com.ke.hs_tracker.module.ui.main

import androidx.fragment.app.activityViewModels
import com.ke.hs_tracker.module.entity.CardBean
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class DeckCardListFragment : CardListFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    override val cardList: StateFlow<List<CardBean>>
        get() = mainViewModel.deckLeftCardList
}