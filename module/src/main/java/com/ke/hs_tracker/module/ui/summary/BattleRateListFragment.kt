package com.ke.hs_tracker.module.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.hi.dhl.binding.viewbind
import com.ke.hs_tracker.module.R
import com.ke.mvvm.base.data.ViewStatus
import com.ke.mvvm.base.databinding.KeMvvmLayoutBaseRefreshListRetryBinding
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import kotlinx.coroutines.flow.collect

abstract class BattleRateListFragment : Fragment() {

    private val adapter = BattleRateItemAdapter()

    private val binding: KeMvvmLayoutBaseRefreshListRetryBinding by viewbind()

    protected abstract val viewModel: BattleRateListViewModel<*>

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(viewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        adapter.addFooterView(
            layoutInflater.inflate(R.layout.module_item_footer_with_fab, null)
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.viewStatus.collect {
                when (it) {
                    is ViewStatus.Loading -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                    is ViewStatus.Content -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        adapter.setList(it.data.sortedByDescending { item ->
                            item.rate
                        })
                    }
                    is ViewStatus.Error -> throw IllegalArgumentException("不该出现错误的情况")
                }
            }
        }
    }
}