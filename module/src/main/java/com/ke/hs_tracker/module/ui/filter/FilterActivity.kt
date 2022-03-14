package com.ke.hs_tracker.module.ui.filter

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivityFilterBinding
import com.ke.hs_tracker.module.databinding.ModuleItemChipFilterBinding
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.hs_tracker.module.entity.Rarity

class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ModuleActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CardClass.values()
            .filter {
                it.display
            }
            .forEach {
                val chip =
                    ModuleItemChipFilterBinding.inflate(layoutInflater).root

                val isBlackTextColor = it.blackText

                if (isBlackTextColor) {
                    chip.setTextColor(Color.BLACK)
                    chip.setCheckedIconResource(R.drawable.module_baseline_done_black_24dp)
                } else {
                    chip.setTextColor(Color.WHITE)
                    chip.setCheckedIconResource(R.drawable.module_baseline_done_white_24dp)
                }
                chip.setChipBackgroundColorResource(it.color)
                chip.setText(it.titleRes)
                binding.chipGroupClass.addView(chip)
            }

        Rarity.values().forEach {
            val chip =
                ModuleItemChipFilterBinding.inflate(layoutInflater).root
            chip.setText(it.title)
            binding.chipGroupRarity.addView(chip)
        }

    }
}