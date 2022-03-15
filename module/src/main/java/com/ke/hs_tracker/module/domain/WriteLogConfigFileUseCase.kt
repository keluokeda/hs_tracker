package com.ke.hs_tracker.module.domain

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.findHSDataFilesDir
import com.ke.hs_tracker.module.log
import com.ke.hs_tracker.module.writeLogConfigFile
import com.ke.mvvm.base.domian.UseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class WriteLogConfigFileUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) :
    UseCase<Boolean, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Boolean): Boolean {

        return context.writeLogConfigFile(parameters)
//        val documentFile = context.findHSDataFilesDir() ?: return false
//
//        val fileName = "log.config"
//        val file = context.findHSDataFilesDir(fileName)
//        if (file != null) {
//            //文件已存在
//            "log.config文件已存在".log()
//
//            if (parameters) {
//                file.delete()
//                val configFile = documentFile.createFile("plain/text", fileName)
//                    ?: return false
//
//                write(configFile)
//            } else {
//                return true
//            }
//        }
//        val configFile = documentFile.createFile("plain/text", fileName)
//            ?: return false
//
//        write(configFile)
//
//        return true
    }

    private fun write(configFile: DocumentFile) {
        context.contentResolver.openOutputStream(configFile.uri)?.use {
            context.assets.open("log.config")
                .copyTo(it)
            it.flush()
        }
    }
}