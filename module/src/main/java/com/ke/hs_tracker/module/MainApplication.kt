package com.ke.hs_tracker.module

import android.app.Application
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import com.ke.hs_tracker.module.data.PreferenceStorage
import com.ke.hs_tracker.module.databinding.ModuleDialogCardPreviewBinding
import com.ke.hs_tracker.module.databinding.ModuleItemCardBinding
import com.ke.hs_tracker.module.entity.Card
import com.ke.hs_tracker.module.parser.PowerParserImpl
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


abstract class MainApplication : Application() {


    @Inject
    lateinit var preferenceStorage: PreferenceStorage

    override fun onCreate() {
        super.onCreate()
//        CrashReport.initCrashReport(applicationContext, "abb84be20b", false)
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
//        val canWriteExternalStorage = ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//
//        return canWriteExternalStorage && canReadDataDir && isExternalStorageManager()
        return canReadDataDir
    }

/**
 * 是否可以访问data目录
 */
val Context.canReadDataDir: Boolean
    get() {
        return DocumentFile.fromTreeUri(applicationContext, HS_DATA_FILE_DIR)?.canRead() ?: false
    }

/**
 * 是否具备外部存储管理权限，如果android版本低于11就返回true
 */
//fun isExternalStorageManager(): Boolean {
//
//    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        Environment.isExternalStorageManager()
//    } else {
//        return true
//    }
//}

const val HS_APPLICATION_ID = "com.blizzard.wtcg.hearthstone"


val HS_DATA_FILE_DIR =
    Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata%2F${HS_APPLICATION_ID}")!!


//const val HUAWEI_HS_APPLICATION_ID = "com.blizzard.wtcg.hearthstone.cn.huawei"

/**
 * 写入log.config文件
 */
suspend fun Context.writeLogConfigFile(forceWrite: Boolean = false): Boolean {
    return withContext(Dispatchers.IO) {


        val documentFile = findHSDataFilesDir() ?: return@withContext false

        val fileName = "log.config"
        val file = findHSDataFilesDir(fileName)
        if (file != null) {
            //文件已存在
            "log.config文件已存在".log()
            if (forceWrite) {
                file.delete()
            } else {
                return@withContext true
            }
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

    ): DocumentFile? {

    DocumentFile.fromTreeUri(
        applicationContext,
        HS_DATA_FILE_DIR
    )?.apply {
//        listFiles().forEach {
//            if (it.name == applicationId) {
        val filesDir = this.findFile("files") ?: return null



        return if (fileName == null) filesDir else filesDir.findFile(
            fileName
        )
//            }
//        }
    }

    return null

//    if (applicationId == HUAWEI_HS_APPLICATION_ID) {
//        return null
//    }
//
//    return findHSDataFilesDir(fileName, HUAWEI_HS_APPLICATION_ID)


}

fun ModuleItemCardBinding.bindCard(card: Card) {
    name.text = card.name
    cost.text = card.cost.toString()

    card.rarity?.apply {
        this@bindCard.cost.setBackgroundColor(
            ResourcesCompat.getColor(
                root.context.resources,
                colorRes,
                null
            )
        )

    }

    Glide.with(imageTile)
        .load("https://art.hearthstonejson.com/v1/tiles/${card.id}.png")
        .into(imageTile)
}


fun showCardImageDialog(context: Context, cardId: String) {
    val binding = ModuleDialogCardPreviewBinding.inflate(LayoutInflater.from(context))
    AlertDialog.Builder(context)
        .show().apply {
            window?.run {
                setContentView(binding.root)
                binding.root.setOnClickListener {
                    dismiss()
                }
                //去掉对话框的白色背景
                setBackgroundDrawableResource(android.R.color.transparent)
            }
        }
    Glide.with(binding.image)
        .load("https://art.hearthstonejson.com/v1/render/latest/zhCN/512x/${cardId}.png")
        .placeholder(R.mipmap.ic_launcher)
        .into(binding.image)
}
