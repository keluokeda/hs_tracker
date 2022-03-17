package com.ke.hs_tracker.module.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import com.hi.dhl.binding.viewbind
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.bindCard
import com.ke.hs_tracker.module.databinding.ModuleFragmentGraveyardBinding
import com.ke.hs_tracker.module.databinding.ModuleItemCardBinding
import com.ke.hs_tracker.module.entity.GraveyardCard
import com.ke.hs_tracker.module.showCardImageDialog
import com.ke.hs_tracker.module.ui.filter.FilterActivity
import com.ke.mvvm.base.ui.BaseViewBindingAdapter
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class GraveyardFragment : Fragment() {


    private val binding: ModuleFragmentGraveyardBinding by viewbind()

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sorted.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                mainViewModel.setSort(SortBy.values()[index])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.filter.setOnClickListener {

            val cardList = mainViewModel.graveyardCardList.value.map {
                it.card
            }

//                listOf(
//                "SCH_270",//始生研习
//                "SCH_235",//衰变飞弹
//                "EX1_610",//爆炸陷阱
//                "TU5_CS2_029",//火球术
//                "CFM_852",//玉莲帮密探
//                "GIL_598",//苔丝
//                "ULD_156t3",//暴龙王
//                "GVG_021",//玛尔加尼斯
//                "FP1_022",//空灵召唤者
//                "CS2_024",//寒冰箭
//                "SCH_427",//雷霆绽放
//                "Story_09_BlastcrystalPotion",//爆晶药水
//                "CORE_CS2_106",//炽炎战斧
//            )
//                .map {
//                    mainViewModel.allCard.find { card ->
//                        card.id == it
//                    }!!
//                }
            val intent = Intent(requireContext(), FilterActivity::class.java)
            intent.putParcelableArrayListExtra(
                FilterActivity.EXTRA_CARD_LIST,
                arrayListOf<Parcelable?>().apply {
                    addAll(cardList)
                })
            startActivity(intent)
        }

        binding.toggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                mainViewModel.toggleShowUserGraveyard(checkedId == R.id.self)
            }
        }

        binding.recyclerView.adapter = adapter

        adapter.setDiffCallback(object : DiffUtil.ItemCallback<GraveyardCard>() {
            override fun areItemsTheSame(oldItem: GraveyardCard, newItem: GraveyardCard): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GraveyardCard,
                newItem: GraveyardCard
            ): Boolean {
                return oldItem == newItem
            }
        })

        adapter.setOnItemClickListener { _, _, position ->

            showCardImageDialog(requireContext(), adapter.getItem(position).card.id)

        }

//        launchAndRepeatWithViewLifecycle {
//            mainViewModel.sortBy.collect { sortBy ->
//                sortList(sortBy)
//            }
//        }

        launchAndRepeatWithViewLifecycle {
            mainViewModel.graveyardCardList.collect {
                adapter.setList(it)
                binding.filter.isEnabled = it.isNotEmpty()
                //更新数据后重新排序下
//                sortList(mainViewModel.sortBy.value)
            }
        }

        launchAndRepeatWithViewLifecycle {
            mainViewModel.showUserGraveyardCardList.collect {
                binding.toggle.check(if (it) R.id.self else R.id.opponent)
            }
        }


    }

//    private fun sortList(sortBy: SortBy) {
//
//
//        binding.sorted.setSelection(SortBy.values().indexOf(sortBy))
//        val list = adapter.data
//        val result = when (sortBy) {
//            SortBy.Cost -> list.sortedBy {
//                it.card.cost
//            }
//            SortBy.CostReverse -> {
//                list.sortedByDescending {
//                    it.card.cost
//                }
//            }
//            SortBy.Time -> {
//                list.sortedBy {
//                    it.time
//                }
//            }
//            SortBy.TimeReverse -> {
//                list.sortedByDescending { it.time }
//            }
//        }
//
//        adapter.setList(result)
//    }

    private val adapter = object : BaseViewBindingAdapter<GraveyardCard, ModuleItemCardBinding>() {
        override fun bindItem(
            item: GraveyardCard,
            viewBinding: ModuleItemCardBinding,
            viewType: Int,
            position: Int
        ) {
            viewBinding.bindCard(item.card)
        }

        override fun createViewBinding(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ): ModuleItemCardBinding {
            return ModuleItemCardBinding.inflate(inflater, parent, false)
        }

    }


}