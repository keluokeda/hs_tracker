package com.ke.hs_tracker.module.ui.deckbattledetail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.hi.dhl.binding.viewbind
import com.ke.hs_tracker.module.databinding.ModuleFragmentDeckBattleDetailDeckDetailBinding
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DeckDetailFragment : Fragment() {
    private val binding: ModuleFragmentDeckBattleDetailDeckDetailBinding by viewbind()

    private val deckDetailViewModel: DeckDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchAndRepeatWithViewLifecycle {
            deckDetailViewModel.costList.collect {
                fillChart(
                    binding.costChart, it
                )
            }
        }

        launchAndRepeatWithViewLifecycle {
            deckDetailViewModel.attackList.collect {
                fillChart(
                    binding.attackChart, it
                )
            }
        }

        launchAndRepeatWithViewLifecycle {
            deckDetailViewModel.mechanics.collect { list ->
                fillChips(
                    binding.mechanicsChips,
                    list.map {
                        it.first.name + it.second
                    }
                )
            }
        }

        launchAndRepeatWithViewLifecycle {
            deckDetailViewModel.raceList.collect { list ->
                fillChips(binding.raceChips, list.map {
                    getString(it.first.titleRes!!) + it.second
                })
            }
        }

        launchAndRepeatWithViewLifecycle {
            deckDetailViewModel.spellSchoolList.collect { list ->
                fillChips(
                    binding.spellSchoolChips,
                    list.map {
                        getString(it.first.titleRes) + it.second
                    }
                )
            }
        }
    }

    private fun fillChips(chipGroup: ChipGroup, strings: List<String>) {
        chipGroup.removeAllViews()
        strings.map {
            Chip(requireContext()).apply {
                text = it
            }
        }.forEach {
            chipGroup.addView(it)
        }

    }

    private fun fillChart(barChart: BarChart, list: List<Pair<Int, Int>>) {
        barChart.description.isEnabled = false
        barChart.xAxis.apply {
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            labelCount = list.size
            textColor = Color.WHITE
        }

        barChart.axisLeft.apply {
            setDrawAxisLine(false)
        }

        barChart.legend.isEnabled = false

        val entryList = list.map {
            BarEntry(it.first.toFloat(), it.second.toFloat())
        }
        val barDataSet = BarDataSet(entryList, "")

//        barDataSet.setValueTextColors(listOf(Color.WHITE))
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 14f
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        barChart.data = BarData(barDataSet)
        barChart.setFitBars(true)
        barChart.invalidate()
    }
}