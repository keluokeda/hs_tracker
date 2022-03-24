package com.ke.hs_tracker.module.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ke.hs_tracker.module.findHSDataFilesDir
import com.ke.hs_tracker.module.log
import com.ke.hs_tracker.module.ui.main.MainActivity
import com.ke.hs_tracker.module.ui.permissions.PermissionsActivity
import com.ke.hs_tracker.module.ui.sync.SyncCardDataActivity
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext


@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val intent = if (hasAllPermissions) {
//            Intent(this, MainActivity::class.java)
//        } else {
//            Intent(this, PermissionsActivity::class.java)
//        }
//        startActivity(intent)

        launchAndRepeatWithViewLifecycle {

//            "写入log文件结果 ${writeLogFile()}".log()

//            val result = writeLogFile()
//
//            "写入文件结果 $result".log()


            splashViewModel.navigationActions.collect {
                val clazz = when (it) {
                    SplashNavigationAction.NavigateToMain -> MainActivity::class.java
                    SplashNavigationAction.NavigateToPermissions -> PermissionsActivity::class.java
                    SplashNavigationAction.NavigateToSync -> SyncCardDataActivity::class.java
                }
                val intent = Intent(this@SplashActivity, clazz)
                startActivity(intent)
            }
        }
    }


    private suspend fun writeLogFile(): Boolean {
        return withContext(Dispatchers.IO) {


            val documentFile = findHSDataFilesDir("Logs") ?: return@withContext false

            val fileName = "Power.log"
            documentFile?.findFile(fileName)?.delete()
            val configFile = documentFile.createFile("plain/text", fileName)
                ?: return@withContext false

            contentResolver.openOutputStream(configFile.uri)?.use {
                assets.open("Power.log")
                    .copyTo(it)
                it.flush()
            }

            return@withContext true
        }


    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}