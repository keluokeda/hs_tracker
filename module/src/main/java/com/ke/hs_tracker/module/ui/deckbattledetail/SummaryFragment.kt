package com.ke.hs_tracker.module.ui.deckbattledetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.hi.dhl.binding.viewbind
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleFragmentDeckBattleDetailSummaryBinding
import com.ke.hs_tracker.module.ui.chart.PieChartData
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * 总览
 */
@AndroidEntryPoint
internal class SummaryFragment : Fragment() {

    private val binding: ModuleFragmentDeckBattleDetailSummaryBinding by viewbind()
    private val summaryViewModel: SummaryViewModel by activityViewModels()

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
            summaryViewModel.heroBattleItems.collect { list ->
                setupPieChart(
                    list.map {
                        PieChartData(
                            getString(it.first.titleRes) + it.second,
                            it.second,
                            it.first.color
                        )
                    },
                    binding.chart
                )
            }
        }
    }

    private fun setupPieChart(list: List<PieChartData>, pieChart: PieChart) {
        pieChart.apply {
            //设置成实心
            holeRadius = 0f
            transparentCircleRadius = 0f
            //禁用描述
            description.isEnabled = false
            legend.isEnabled = true
            legend.textColor = resources.getColor(R.color.module_grey500, null)
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP

            setEntryLabelColor(resources.getColor(R.color.module_grey500, null))
            setUsePercentValues(true)

            val pieDataSet = PieDataSet(list.map {
                return@map PieEntry(
                    it.value.toFloat(), it.label
                )
            }, "")
            pieDataSet.valueTextSize = 16f
            pieDataSet.valueTextColor = resources.getColor(R.color.module_grey500, null)


            pieDataSet.colors = list.map {
                it.color
            }.map {
                resources.getColor(it, null)
            }

            val pieData = PieData(pieDataSet)
//            pieData.setDrawValues(true)
            pieData.setValueFormatter(PercentFormatter(this))

//            pieData.setValueFormatter(object : ValueFormatter() {
//                override fun getFormattedValue(value: Float): String {
//                    return "$value %"
//                }
//            })

            data = pieData
            invalidate()
        }
    }

}