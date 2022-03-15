package com.ke.hs_tracker.module.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ke.hs_tracker.module.BuildConfig
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivitySettingsBinding
import com.ke.hs_tracker.module.ui.deck.DeckCodeParserActivity
import com.ke.hs_tracker.module.ui.sync.SyncCardDataActivity
import com.ke.hs_tracker.module.ui.test.TestActivity
import com.ke.hs_tracker.module.ui.theme.ThemeActivity
import com.ke.hs_tracker.module.ui.writeconfig.WriteConfigActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ModuleActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)

        }
        setTitle(R.string.module_settings)
        binding.test.isVisible = BuildConfig.DEBUG
        binding.test.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        binding.theme.setOnClickListener {
            startActivity(Intent(this, ThemeActivity::class.java))
        }

        binding.codeParser.setOnClickListener {
            startActivity(Intent(this, DeckCodeParserActivity::class.java))

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
}