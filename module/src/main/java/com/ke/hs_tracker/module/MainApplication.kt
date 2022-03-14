package com.ke.hs_tracker.module

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.ke.hs_tracker.module.data.PreferenceStorage
import com.ke.hs_tracker.module.parser.PowerParserImpl
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.bugly.crashreport.CrashReport
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


@HiltAndroidApp
class MainApplication : Application() {


    @Inject
    lateinit var preferenceStorage: PreferenceStorage

    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(applicationContext, "abb84be20b", BuildConfig.DEBUG)
        AppCompatDelegate.setDefaultNightMode(preferenceStorage.theme)
        Logger.addLogAdapter(
            AndroidLogAdapter(
                PrettyFormatStrategy.newBuilder()
                    .methodCount(5)
                    .build()
            )
        )
    }
}

fun String.removeTime(): Triple<String, Date, String> {

    val content = substring(PowerParserImpl.TIME_PREFIX_SIZE)
    val start = substring(0, 1)
    val hms = substring(2, 10).split(":")
    val calendar = Calendar.getInstance()
    calendar.set(
        Calendar.HOUR_OF_DAY, hms[0].toInt()
    )
    calendar.set(
        Calendar.MINUTE, hms[1].toInt()
    )
    calendar.set(
        Calendar.SECOND, hms[2].toInt()
    )

    return Triple(
        start,
        calendar.time,
        content
    )
}


fun String.log() {
    Logger.d(this)
}

/**
 * 是否具备所有权限
 */
val Context.hasAllPermissions: Boolean
    get() {
        val canWriteExternalStorage = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return canWriteExternalStorage && canReadDataDir && isExternalStorageManager()
    }

/**
 * 是否可以访问data目录
 */
val Context.canReadDataDir: Boolean
    get() {
        return DocumentFile.fromTreeUri(applicationContext, DATA_DIR_URI)?.canRead() ?: false
    }

/**
 * 是否具备外部存储管理权限，如果android版本低于11就返回true
 */
fun isExternalStorageManager(): Boolean {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        return true
    }
}

val DATA_DIR_URI =
    Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata")!!

const val HS_APPLICATION_ID = "com.blizzard.wtcg.hearthstone"

/**
 * 写入log.config文件
 */
suspend fun Context.writeLogConfigFile(): Boolean {
    return withContext(Dispatchers.IO) {


        val documentFile = findHSDataFilesDir() ?: return@withContext false

        val fileName = "log.config"
        val file = findHSDataFilesDir(fileName)
        if (file != null) {
            //文件已存在
            "log.config文件已存在 不需要写入".log()
            return@withContext true
        }
        val configFile = documentFile.createFile("plain/text", fileName)
            ?: return@withContext false

        contentResolver.openOutputStream(configFile.uri)?.use {
            assets.open("log.config")
                .copyTo(it)
            it.flush()
        }

        return@withContext true
    }


}

//通过从根目录进入的方式可以创建文件
fun Context.findHSDataFilesDir(

    fileName: String? = null,
    applicationId: String
    = HS_APPLICATION_ID,
): DocumentFile? {

    DocumentFile.fromTreeUri(
        applicationContext,
        DATA_DIR_URI
    )?.apply {
        listFiles().forEach {
            if (it.name == applicationId) {
                val filesDir = it.findFile("files") ?: return null
                return if (fileName == null) filesDir else filesDir.findFile(
                    fileName
                )
            }
        }
    }

    return null


}

