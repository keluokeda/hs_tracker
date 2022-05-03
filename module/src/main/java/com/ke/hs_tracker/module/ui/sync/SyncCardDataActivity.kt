package com.ke.hs_tracker.module.ui.sync

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivitySyncCardDataBinding
import com.ke.hs_tracker.module.ui.summary.SummaryActivity
import com.ke.mvvm.base.ui.collectLoadingDialog
import com.ke.mvvm.base.ui.collectSnackbarFlow
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SyncCardDataActivity : AppCompatActivity() {
    private lateinit var binding: ModuleActivitySyncCardDataBinding

    private val viewModel: SyncCardDataViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivitySyncCardDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (viewModel.showBackButton) {
            binding.toolbar.setNavigationIcon(R.drawable.module_baseline_arrow_back_white_24dp)
            binding.toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }

        collectLoadingDialog(viewModel)
        collectSnackbarFlow(viewModel)

        binding.sync.setOnClickListener {
            viewModel.sync(
                binding.version.text?.toString() ?: "",
                if (binding.chinese.isChecked) "zhCN" else "enUS"
            )
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.navigationActions.collect {
                val action: () -> Unit = when (it) {
                    SyncCardDataNavigationAction.NavigateToBack -> {
                        {
                            onBackPressed()
                        }
                    }
                    SyncCardDataNavigationAction.NavigateToMain -> {
                        {
                            val intent =
                                Intent(this@SyncCardDataActivity, SummaryActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                AlertDialog.Builder(this@SyncCardDataActivity)
                    .setTitle(R.string.module_hint)
                    .setMessage(R.string.module_sync_success)
                    .setOnDismissListener {
                        action()
                    }.setPositiveButton(R.string.module_done, null)
                    .show()


            }
        }

    }

    companion object {
        const val EXTRA_SHOW_BACK_BUTTON = "EXTRA_SHOW_BACK_BUTTON"
    }

}