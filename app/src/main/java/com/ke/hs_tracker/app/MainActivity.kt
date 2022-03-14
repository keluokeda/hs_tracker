package com.ke.hs_tracker.app
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.documentfile.provider.DocumentFile
//import androidx.lifecycle.lifecycleScope
//import com.ke.hs_tracker.app.databinding.ActivityMainBinding
//import com.ke.hs_tracker.module.findHSDataFilesDir
//import com.ke.hs_tracker.module.log
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class MainActivity : AppCompatActivity() {
//
//
//    private lateinit var powerLogFile: DocumentFile
//
//    private lateinit var binding: ActivityMainBinding
//
////    private lateinit var inputStream: InputStream
//
//    private var oldLogSize = 0L
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//
//        binding.init.setOnClickListener {
//            lifecycleScope.launch {
//
//                oldLogSize = 0
//                val logsDir = findHSDataFilesDir("Logs")
//
//                val file = logsDir?.findFile("Power.log")
//                if (file != null) {
//                    "初始化成功 $file".log()
//                    powerLogFile = file
//                } else {
//                    "初始化失败".log()
//                }
//
//
//            }
//        }
//
//        binding.delete.setOnClickListener {
//            try {
//                "删除本地日志结果 ${deleteFile("power.log")}".log()
//
//                val result = powerLogFile.delete()
//                "删除文件结果 $result".log()
//            } catch (e: Exception) {
////                binding.content.text = e.message
//                "删除文件失败".log()
//                e.printStackTrace()
//            }
//        }
//
//        binding.load.setOnClickListener {
//
//            lifecycleScope.launch {
//                try {
//
//
//                    contentResolver.openInputStream(powerLogFile.uri)!!.reader()
//                        .apply {
//                            if (oldLogSize > 0) {
//                                val skip = skip(oldLogSize)
//                                "跳过的字节数量 $skip".log()
//                            }
//                            binding.content.text = readText().also {
//                                oldLogSize += it.length
//                                writeToLocal(it)
//                            }
//
//                            close()
//                        }
//
//                } catch (e: Exception) {
//                    binding.content.text = e.message
//                }
//            }
//
//
//        }
//    }
//
//    private suspend fun writeToLocal(content: String) {
//        withContext(Dispatchers.IO) {
//            openFileOutput("power.log", MODE_APPEND)
//                .writer().apply {
//                    append(content)
//                    flush()
//                    close()
//                }
//        }
//    }
//
//
////    private suspend fun getPowerLogFileSize(): Long {
////        return withContext(Dispatchers.IO) {
////            try {
////                contentResolver.query(powerLogFile.uri, arrayOf(OpenableColumns.SIZE), null, null)
////                    ?.apply {
////                        moveToFirst()
////                        return@withContext getLong(0).also {
////                            close()
////                        }
////
////                    }
////            } catch (e: Exception) {
////                e.printStackTrace()
////                return@withContext 0
////            }
////
////
////
////            0
////        }
////    }
//}