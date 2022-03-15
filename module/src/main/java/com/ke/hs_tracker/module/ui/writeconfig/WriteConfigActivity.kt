package com.ke.hs_tracker.module.ui.writeconfig

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivityWriteConfigBinding
import com.ke.hs_tracker.module.ui.sync.SyncCardDataActivity
import com.ke.hs_tracker.module.writeLogConfigFile
import kotlinx.coroutines.launch

class WriteConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.module_activity_write_config)
        val binding = ModuleActivityWriteConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rewrite = intent.getBooleanExtra(EXTRA_REWRITE, false)

        if (rewrite) {
            binding.forceWrite.isEnabled = false
            binding.forceWrite.isChecked = true
            binding.toolbar.apply {
                setNavigationIcon(R.drawable.module_baseline_arrow_back_white_24dp)
                setNavigationOnClickListener {
                    onBackPressed()
                }
            }
        }

        binding.content.text = assets.open("log.config").reader().readText()

        binding.writeIn.setOnClickListener {
//            findHSDataFilesDir("log.config")
            lifecycleScope.launch {
                if (writeLogConfigFile(
                        binding.forceWrite.isChecked
                    )
                ) {
                    if (rewrite) {
                        onBackPressed()
                    } else {
                        startActivity(
                            Intent(
                                this@WriteConfigActivity,
                                SyncCardDataActivity::class.java
                            )
                        )
                        finish()
                    }
                } else {
                    AlertDialog.Builder(this@WriteConfigActivity)
                        .setTitle(R.string.module_hint)
                        .setMessage(R.string.module_write_config_failed)
                        .setPositiveButton(R.string.module_done, null)
                        .show()
                }

            }
        }

    }

    companion object {
        const val EXTRA_REWRITE = "EXTRA_REWRITE"
    }
}