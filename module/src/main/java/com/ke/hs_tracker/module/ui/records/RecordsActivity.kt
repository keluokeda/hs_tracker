package com.ke.hs_tracker.module.ui.records

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.ke.hs_tracker.module.databinding.ModuleActivityRecordsBinding
import com.ke.hs_tracker.module.ui.zoneevents.ZoneEventsActivity
import com.ke.mvvm.base.data.ViewStatus
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RecordsActivity : AppCompatActivity() {

    private val adapter by lazy {
       RecordAdapter().apply {


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

