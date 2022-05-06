package com.ke.hs_tracker.module.ui.deckbattledetail

import androidx.fragment.app.activityViewModels
import com.ke.hs_tracker.module.entity.CardBean
import com.ke.hs_tracker.module.ui.main.CardListFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class DeckFragment : CardListFragment() {

    private val deckViewModel: DeckViewModel by activityViewModels()

    override val cardList: StateFlow<List<CardBean>>
        get() = deckViewModel.cardList
}