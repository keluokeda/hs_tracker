package com.ke.hs_tracker.module.ui.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.ke.hs_tracker.module.databinding.ModuleActivityTestBinding
import com.ke.hs_tracker.module.findHSDataFilesDir
import com.ke.hs_tracker.module.parser.PowerParser
import com.ke.hs_tracker.module.parser.PowerParserImpl
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter

class TestActivity : AppCompatActivity() {
    private lateinit var binding: ModuleActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val powerParser: PowerParser = PowerParserImpl()

        val lineList = mutableListOf<String>()

        lineList.clear()
        lineList.addAll(
            assets.open("Power.log").reader().readLines().toMutableList()
        )


        val document = findHSDataFilesDir("Logs")?.findFile("Power.log")

        var writer: OutputStreamWriter? = null
        document?.apply {

            writer = contentResolver.openOutputStream(uri, "wa")
                ?.writer()
        }

        binding.clear.setOnClickListener {
            document?.apply {
                contentResolver.openOutputStream(uri, "wt")?.writer()?.write("")

            }
            lineList.clear()
            lineList.addAll(
                assets.open("Power.log").reader().readLines().toMutableList()
            )

        }

//        powerParser.powerTagListener = {
//
//
//        }
        binding.next.setOnClickListener {
//                val target = mutableListOf<String>()
            repeat(1000) {
                val line = lineList.removeFirstOrNull()
                if (line != null) {
//                        target.add(line)
                    writer?.appendLine(line)
//                    powerParser.parse(line)
                }
            }


        }
    }
}