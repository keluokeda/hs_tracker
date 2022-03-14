package com.ke.hs_tracker.module.ui.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.ke.hs_tracker.module.databinding.ModuleActivityTestBinding
import com.ke.hs_tracker.module.parser.PowerParser
import com.ke.hs_tracker.module.parser.PowerParserImpl

class TestActivity : AppCompatActivity() {
    private lateinit var binding: ModuleActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val powerParser: PowerParser = PowerParserImpl()

        val lineList = assets.open("Power.log").reader().readLines().toMutableList()

        powerParser.powerTagListener = {


        }
        binding.next.setOnClickListener {
            repeat(100) {
                val line = lineList.removeFirstOrNull()
                if (line != null) {
                    powerParser.parse(line)
                }
            }
        }
    }
}