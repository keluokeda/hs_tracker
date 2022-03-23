package com.ke.hs_tracker.module.ui.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.ke.hs_tracker.module.databinding.ModuleActivityTestBinding
import com.ke.hs_tracker.module.findHSDataFilesDir
import com.ke.hs_tracker.module.log
import com.ke.hs_tracker.module.parser.PowerParser
import com.ke.hs_tracker.module.parser.PowerParserImpl
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

        val logDocument = findHSDataFilesDir("Logs")


        var document = logDocument?.findFile("Power.log")
        if (document == null) {
            document = logDocument?.createFile("plain/text", "Power.log")
        }

        var writer: OutputStreamWriter? = null
        document?.apply {

            writer = contentResolver.openOutputStream(uri, "wa")
                ?.writer()
        }

        binding.clear.setOnClickListener {
            document?.apply {
                contentResolver.openOutputStream(uri, "wt")?.writer()?.write("")

            }

            assets.open("Power.log").reader().apply {
                lineList.clear()

                lineList.addAll(readLines().toMutableList())
                close()
            }


        }


//        powerParser.powerTagListener = {
//
//
//        }
        var counter = 0
        binding.next.setOnClickListener {
//                val target = mutableListOf<String>()
            repeat(1024) {
                counter++
                val line = lineList.removeFirstOrNull()
                if (line != null) {
//                        target.add(line)
                    writer?.appendLine(line)
//                    powerParser.parse(line)
                }
            }
            writer?.flush()


        }
    }
}