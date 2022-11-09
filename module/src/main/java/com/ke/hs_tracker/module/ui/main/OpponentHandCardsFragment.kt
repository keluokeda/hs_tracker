package com.ke.hs_tracker.module.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hi.dhl.binding.viewbind
import com.ke.hs_tracker.module.databinding.ModuleFragmentOpponentHandCardsBinding
import com.ke.hs_tracker.module.databinding.ModuleItemOpponentHandCardBinding
import com.ke.mvvm.base.ui.BaseViewBindingAdapter
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OpponentHandCardsFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val binding: ModuleFragmentOpponentHandCardsBinding by viewbind()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    private val adapter =
        object : BaseViewBindingAdapter<OpponentHandCard, ModuleItemOpponentHandCardBinding>() {
            override fun bindItem(
                item: OpponentHandCard,
                viewBinding: ModuleItemOpponentHandCardBinding,
                viewType: Int,
                position: Int
            ) {
                viewBinding.text.text = (item.turn).toString()
            }

            override fun createViewBinding(
                inflater: LayoutInflater,
                parent: ViewGroup,
                viewType: Int
            ): ModuleItemOpponentHandCardBinding {
                return ModuleItemOpponentHandCardBinding.inflate(inflater, parent, false)
            }

        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter

        launchAndRepeatWithViewLifecycle {
            mainViewModel.opponentHandCards.collect {
                adapter.setList(
                    it.second
                        .sortedBy { card ->
                            card.position
                        }
                        .sortedBy { card ->
                            card.time
                        }
                )
            }
        }
    }
}