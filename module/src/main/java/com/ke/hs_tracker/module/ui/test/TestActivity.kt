package com.ke.hs_tracker.module.ui.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ke.hs_tracker.module.databinding.ModuleActivityTestBinding
import com.ke.hs_tracker.module.findHSDataFilesDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestActivity : AppCompatActivity() {
    private lateinit var binding: ModuleActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.createRecord.setOnClickListener {
            startActivity(Intent(this, CreateRecordActivity::class.java))
        }

        binding.clearLog.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val documentFile = findHSDataFilesDir("Logs")?.findFile("Power.log")
//                        getLogsDir()?.findFile(powerFileName)
                    documentFile?.apply {
                        contentResolver.openOutputStream(uri, "wt")?.use {
                            it.write("".encodeToByteArray())
                            it.flush()
                            it.close()
                        }
                    }
                }


                AlertDialog.Builder(this@TestActivity)
                    .setTitle("提示")
                    .setMessage("清除成功")
                    .setPositiveButton("确定", null)
                    .show()
            }

        }
//        val powerParser: PowerParser = PowerParserImpl()

//        val lineList = mutableListOf<String>()
//
//        lineList.clear()
//        lineList.addAll(
//            assets.open("Power.log").reader().readLines().toMutableList()
//        )
//
//        val logDocument = findHSDataFilesDir("Logs")
//
//
//        var document = logDocument?.findFile("Power.log")
//        if (document == null) {
//            document = logDocument?.createFile("plain/text", "Power.log")
//        }
//
//        var writer: OutputStreamWriter? = null
//        document?.apply {
//
//            writer = contentResolver.openOutputStream(uri, "wa")
//                ?.writer()
//        }

//        binding.clear.setOnClickListener {
//            document?.apply {
//                contentResolver.openOutputStream(uri, "wt")?.writer()?.write("")
//
//            }
//
//            assets.open("Power.log").reader().apply {
//                lineList.clear()
//
//                lineList.addAll(readLines().toMutableList())
//                close()
//            }
//
//
//        }


//        powerParser.powerTagListener = {
//
//
//        }
//        var counter = 0
//        binding.next.setOnClickListener {
////                val target = mutableListOf<String>()
//            repeat(1024) {
//                counter++
//                val line = lineList.removeFirstOrNull()
//                if (line != null) {
////                        target.add(line)
//                    writer?.appendLine(line)
////                    powerParser.parse(line)
//                }
//            }
//            writer?.flush()
//
//
//        }
    }
}