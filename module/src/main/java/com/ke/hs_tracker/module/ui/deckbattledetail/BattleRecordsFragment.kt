package com.ke.hs_tracker.module.ui.deckbattledetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.hi.dhl.binding.viewbind
import com.ke.hs_tracker.module.ui.records.RecordAdapter
import com.ke.mvvm.base.databinding.KeMvvmLayoutBaseRefreshListRetryBinding
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BattleRecordsFragment : Fragment() {
    private val battleRecordsViewModel: BattleRecordsViewModel by activityViewModels()

    private val binding: KeMvvmLayoutBaseRefreshListRetryBinding by viewbind()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private val adapter by lazy {
        RecordAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefreshLayout.isEnabled = false
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        launchAndRepeatWithViewLifecycle {
            battleRecordsViewModel.games.collect {
                adapter.setList(it)
            }
        }
    }
}