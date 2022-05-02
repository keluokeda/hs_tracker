package com.ke.hs_tracker.module.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivitySettingsBinding
import com.ke.hs_tracker.module.ui.deck.DeckCodeParserActivity
import com.ke.hs_tracker.module.ui.migrate.MigrateMainActivity
import com.ke.hs_tracker.module.ui.sync.SyncCardDataActivity
import com.ke.hs_tracker.module.ui.test.TestActivity
import com.ke.hs_tracker.module.ui.theme.ThemeActivity
import com.ke.hs_tracker.module.ui.writeconfig.WriteConfigActivity
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {
    private lateinit var binding: ModuleActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        launchAndRepeatWithViewLifecycle {
            settingsViewModel.saveLogFileEnable.collect {
                binding.saveLogFile.setOnCheckedChangeListener(null)
                binding.saveLogFile.isChecked = it
                binding.saveLogFile.setOnCheckedChangeListener(this@SettingsActivity)
            }
        }

        binding.toolbar.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            this.menu.add(0, 0, 0, "测试")
            this.setOnMenuItemClickListener {
                startActivity(Intent(this@SettingsActivity, TestActivity::class.java))

                true
            }
        }


        binding.theme.setOnClickListener {
            startActivity(Intent(this, ThemeActivity::class.java))
        }

        binding.codeParser.setOnClickListener {
            startActivity(Intent(this, DeckCodeParserActivity::class.java))

        }

        binding.migrate.setOnClickListener {
            startActivity(Intent(this, MigrateMainActivity::class.java))
        }

        binding.rewriteConfigFile.setOnClickListener {
            val intent = Intent(this, WriteConfigActivity::class.java)
            intent.putExtra(WriteConfigActivity.EXTRA_REWRITE, true)
            startActivity(intent)
        }

        binding.sync.setOnClickListener {
            val intent = Intent(this, SyncCardDataActivity::class.java)
            intent.putExtra(SyncCardDataActivity.EXTRA_SHOW_BACK_BUTTON, true)
            startActivity(intent)
        }

        //给作者发邮件
        binding.llContactAuthor.setOnClickListener {
            val email = getString(R.string.module_author_email)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            startActivity(Intent.createChooser(intent, "Send To"))
        }

    }

    override fun onCheckedChanged(p0: CompoundButton, checked: Boolean) {
        settingsViewModel.setSaveLogFileEnable(checked)
    }
}