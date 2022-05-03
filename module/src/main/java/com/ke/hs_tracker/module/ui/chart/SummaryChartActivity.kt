package com.ke.hs_tracker.module.ui.chart

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivitySummaryChartBinding
import com.ke.mvvm.base.data.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SummaryChartActivity : AppCompatActivity() {


    @Inject
    internal lateinit var getSummaryChartViewDataUseCase: GetSummaryChartViewDataUseCase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.module_activity_summary_chart)
        val binding: ModuleActivitySummaryChartBinding =
            ModuleActivitySummaryChartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

//        binding.all.apply {
//            //设置成实心
//            holeRadius = 0f
//            transparentCircleRadius = 0f
//            //禁用描述
//            description.isEnabled = false
//            //禁用颜色描述
//            legend.isEnabled = false
//            setUsePercentValues(true)
//            val pieEntryList = mutableListOf<PieEntry>()
//            pieEntryList.add(PieEntry(41f, "胜利41"))
//            pieEntryList.add(PieEntry(60f, "失败60"))
//            val pieDataSet = PieDataSet(pieEntryList, "")
//            pieDataSet.valueTextSize = 20f
//            pieDataSet.valueTextColor = Color.WHITE
//
//            pieDataSet.colors = listOf(R.color.module_win, R.color.module_loss).map {
//                resources.getColor(it, null)
//            }
//            val pieData = PieData(pieDataSet)
//            //显示次数
//            pieData.setDrawValues(true)
//            pieData.setValueFormatter(PercentFormatter())
//            data = pieData
//            invalidate()
//        }


        lifecycleScope.launch {
            val result = getSummaryChartViewDataUseCase(Unit)
            when (result) {
                is Result.Success -> {
                    setChartData(result.data, binding)
                }
                is Result.Error -> {
                    result.exception.printStackTrace()
                }
            }
        }


    }

    private fun setChartData(
        summaryChartViewData: SummaryChartViewData,
        binding: ModuleActivitySummaryChartBinding
    ) {

        setupPieChart(
            listOf(
                PieChartData(
                    "${getString(R.string.module_win)}${summaryChartViewData.winCount}",
                    summaryChartViewData.winCount,
                    R.color.module_win
                ),
                PieChartData(
                    "${getString(R.string.module_loss)}${summaryChartViewData.lossCount}",
                    summaryChartViewData.lossCount,
                    R.color.module_loss
                ),
            ),
            binding.all
        )

        setupPieChart(
            listOf(
                PieChartData(
                    "${getString(R.string.module_first_hand)}${summaryChartViewData.firstHandCount}",
                    summaryChartViewData.firstHandCount,
                    R.color.module_win
                ),
                PieChartData(
                    "${getString(R.string.module_second_hand)}${summaryChartViewData.secondHandCount}",
                    summaryChartViewData.secondHandCount,
                    R.color.module_loss
                ),
            ),
            binding.firstHandPercent
        )

        setupPieChart(
            listOf(
                PieChartData(
                    "${getString(R.string.module_win)}${summaryChartViewData.firstHandWinCount}",
                    summaryChartViewData.firstHandWinCount,
                    R.color.module_win
                ),
                PieChartData(
                    "${getString(R.string.module_loss)}${summaryChartViewData.firstHandLossCount}",
                    summaryChartViewData.firstHandLossCount,
                    R.color.module_loss
                ),
            ),
            binding.firstHandRate
        )

        setupPieChart(
            listOf(
                PieChartData(
                    "${getString(R.string.module_win)}${summaryChartViewData.secondHandWinCount}",
                    summaryChartViewData.secondHandWinCount,
                    R.color.module_win
                ),
                PieChartData(
                    "${getString(R.string.module_loss)}${summaryChartViewData.secondHandLossCount}",
                    summaryChartViewData.secondHandLossCount,
                    R.color.module_loss
                ),
            ),
            binding.secondHandRate
        )

        setupPieChart(
            summaryChartViewData.classCounts.map {
                PieChartData(
                    getString(it.first.titleRes) + it.second,
                    it.second,
                    it.first.color
                )
            },
            binding.classDistribution
        )
    }

    private fun setupPieChart(list: List<PieChartData>, pieChart: PieChart) {
        pieChart.apply {
            //设置成实心
            holeRadius = 0f
            transparentCircleRadius = 0f
            //禁用描述
            description.isEnabled = false
            //禁用颜色描述
            legend.isEnabled = false
            setUsePercentValues(true)

            val pieDataSet = PieDataSet(list.map {
                return@map PieEntry(
                    it.value.toFloat(), it.label
                )
            }, "")
            pieDataSet.valueTextSize = 16f
            pieDataSet.valueTextColor = Color.WHITE

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