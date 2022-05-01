package com.ke.hs_tracker.module.ui.summary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivitySummaryBinding
import com.ke.hs_tracker.module.databinding.ModuleHeaderSummaryBinding
import com.ke.hs_tracker.module.databinding.ModuleItemSummaryBattleBinding
import com.ke.hs_tracker.module.ui.main.MainActivity
import com.ke.hs_tracker.module.ui.records.RecordsActivity
import com.ke.hs_tracker.module.ui.settings.SettingsActivity
import com.ke.mvvm.base.data.ViewStatus
import com.ke.mvvm.base.ui.BaseViewBindingAdapter
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SummaryActivity : AppCompatActivity() {

    private val adapter =
        object : BaseViewBindingAdapter<HeroBattleItem, ModuleItemSummaryBattleBinding>() {
            override fun bindItem(
                item: HeroBattleItem,
                viewBinding: ModuleItemSummaryBattleBinding,
                viewType: Int,
                position: Int
            ) {
                viewBinding.apply {
                    allCount.text = "总：" + (item.lostCount + item.winCount).toString()
                    winCount.text = "胜：" + item.winCount.toString()
                    lostCount.text = "负：" + item.lostCount.toString()
                    image.setImageResource(item.hero.roundIcon!!)
                    name.setText(item.hero.titleRes)
                    winRate.text = item.rate.toString() + "%"
                }
            }

            override fun createViewBinding(
                inflater: LayoutInflater,
                parent: ViewGroup,
                viewType: Int
            ): ModuleItemSummaryBattleBinding {
                return ModuleItemSummaryBattleBinding.inflate(inflater, parent, false)
            }

        }

    private lateinit var binding: ModuleActivitySummaryBinding
    private val summaryViewModel: SummaryViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter.addFooterView(
            layoutInflater.inflate(R.layout.module_item_footer_with_fab, null)
        )

        binding.toolbar.apply {
            menu.clear()
            menu.add(
                0,
                0,
                0,
                R.string.module_settings
            ).setIcon(R.drawable.module_baseline_settings_white_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            menu.add(0, 1, 0, "查看所有对局")

            setOnMenuItemClickListener {
                if (it.itemId == 1) {
                    startActivity(Intent(this@SummaryActivity, RecordsActivity::class.java))
                } else if (it.itemId == 0) {
                    startActivity(Intent(this@SummaryActivity, SettingsActivity::class.java))
                }
                true
            }
        }

        binding.start.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            summaryViewModel.refresh()
        }
        lifecycle.addObserver(summaryViewModel)
        launchAndRepeatWithViewLifecycle {
            summaryViewModel.viewStatus.collect {
                when (it) {
                    is ViewStatus.Loading -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                    is ViewStatus.Content -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        adapter.removeAllHeaderView()
                        val headerBinding = ModuleHeaderSummaryBinding.inflate(layoutInflater)
                        headerBinding.allCount.text =
                            "总：" + (it.data.lostCount + it.data.winCount).toString()
                        headerBinding.winCount.text = "胜：" + it.data.winCount.toString()
                        headerBinding.lostCount.text = "负：" + it.data.lostCount.toString()
                        headerBinding.allWinRate.text = it.data.rate.toString()
                        adapter.addHeaderView(headerBinding.root)
                        adapter.setList(it.data.list)
                    }
                    is ViewStatus.Error -> throw IllegalArgumentException("不该出现错误的情况")
                }
            }
        }

    }


}