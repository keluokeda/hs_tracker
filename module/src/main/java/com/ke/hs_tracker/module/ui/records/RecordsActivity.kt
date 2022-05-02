package com.ke.hs_tracker.module.ui.records

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivityRecordsBinding
import com.ke.hs_tracker.module.databinding.ModuleItemRecordBinding
import com.ke.hs_tracker.module.db.Game
import com.ke.hs_tracker.module.ui.zoneevents.ZoneEventsActivity
import com.ke.mvvm.base.data.ViewStatus
import com.ke.mvvm.base.ui.BaseViewBindingAdapter
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RecordsActivity : AppCompatActivity() {

    private val adapter by lazy {
        object : BaseViewBindingAdapter<Game, ModuleItemRecordBinding>() {
            override fun bindItem(
                item: Game,
                viewBinding: ModuleItemRecordBinding,
                viewType: Int,
                position: Int
            ) {
                viewBinding.apply {
                    userHero.setImageResource(item.userHero!!.roundIcon!!)
                    opponentHero.setImageResource(item.opponentHero!!.roundIcon!!)
                    date.text = simpleDateFormat.format(Date(item.startTime))
                    state.isEnabled = item.isUserWin ?: true
                    val type = getString(item.formatType.title) + getString(item.gameType.title)
                    gameType.text = type
                    state.setText(if (item.isUserWin == true) R.string.module_win else R.string.module_loss)
                }
            }

            override fun createViewBinding(
                inflater: LayoutInflater,
                parent: ViewGroup,
                viewType: Int
            ): ModuleItemRecordBinding {
                return ModuleItemRecordBinding.inflate(inflater, parent, false)
            }

        }.apply {


            setOnItemClickListener { _, _, position ->
                startActivity(Intent(this@RecordsActivity, ZoneEventsActivity::class.java).apply {
                    putExtra(ZoneEventsActivity.EXTRA_KEY_ID, getItem(position).id)
                })
            }
        }
    }

    private val recordsViewModel: RecordsViewModel by viewModels()
    private lateinit var binding: ModuleActivityRecordsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


//        binding.toolbar.menu.apply {
//            clear()
//            add(
//                0,
//                0,
//                0,
//                R.string.module_settings
//            ).setIcon(R.drawable.module_baseline_settings_white_24dp)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//
//
//        }
//        binding.toolbar.setOnMenuItemClickListener {
//            startActivity(Intent(this, SettingsActivity::class.java))
//            true
//        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            recordsViewModel.loadData()
        }

        launchAndRepeatWithViewLifecycle {
            recordsViewModel.viewStatus.collect {
                binding.swipeRefreshLayout.isRefreshing = it is ViewStatus.Loading
                when (it) {
                    is ViewStatus.Loading -> {

                    }
                    is ViewStatus.Content -> {
                        adapter.setList(it.data)
                    }
                    is ViewStatus.Error -> {

                    }
                }
            }
        }
    }
}

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")