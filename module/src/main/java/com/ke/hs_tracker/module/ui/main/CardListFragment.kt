package com.ke.hs_tracker.module.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.hi.dhl.binding.viewbind
import com.ke.hs_tracker.module.databinding.ModuleFragmentCardListBinding
import com.ke.hs_tracker.module.databinding.ModuleItemCardBinding
import com.ke.hs_tracker.module.entity.CardBean
import com.ke.hs_tracker.module.ui.common.CardAdapter
import com.ke.mvvm.base.ui.BaseViewBindingAdapter
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

abstract class CardListFragment : Fragment() {

    private val adapter by lazy {
        CardAdapter()
    }


    private val binding: ModuleFragmentCardListBinding by viewbind()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        launchAndRepeatWithViewLifecycle {
            cardList.collect {
                adapter.setList(it)
            }
        }
    }


    abstract val cardList: StateFlow<List<CardBean>>
}