package com.ke.hs_tracker.module.ui.diagnose

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivityDiagnoseBinding
import com.ke.hs_tracker.module.findHSDataFilesDir

/**
 * 诊断
 */
class DiagnoseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.module_activity_diagnose)
        val binding = ModuleActivityDiagnoseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.checkConfigFile.setOnClickListener {
            var message = ""
            val logsDir = findHSDataFilesDir("log.config")
            if (logsDir == null) {
                message = "无法找到log.config文件"
            } else {

            }
        }
    }
}