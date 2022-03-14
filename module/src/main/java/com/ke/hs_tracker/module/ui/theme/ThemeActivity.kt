package com.ke.hs_tracker.module.ui.theme

import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ke.hs_tracker.module.data.PreferenceStorage
import com.ke.hs_tracker.module.databinding.ModuleActivityThemeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThemeActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    @Inject
    lateinit var preferenceStorage: PreferenceStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ModuleActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.light.tag = AppCompatDelegate.MODE_NIGHT_NO
        binding.dark.tag = AppCompatDelegate.MODE_NIGHT_YES
        binding.system.tag = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

        val buttonList = listOf(
            binding.light,
            binding.dark,
            binding.system
        )

        buttonList.forEach {
            it.isChecked = it.tag == preferenceStorage.theme
        }

        buttonList.forEach {
            it.setOnCheckedChangeListener(this)
        }


    }

    override fun onCheckedChanged(button: CompoundButton, checked: Boolean) {

        if (checked) {
            val theme = button.tag as? Int ?: return
            preferenceStorage.theme = theme
        }
    }
}