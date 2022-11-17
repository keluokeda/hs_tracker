package com.ke.hs_tracker.module.domain

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.findHSDataFilesDir
import com.ke.mvvm.base.domian.UseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File
import javax.inject.Inject

class GetLocalLogDirUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) :
    UseCase<Unit, DocumentFile?>(dispatcher) {

    override suspend fun execute(parameters: Unit): DocumentFile? {
        val logsDir = context.getExternalFilesDir("Logs")!!
        if (!logsDir.exists()) {
            logsDir.mkdir()

        }
//        val decksFile = File(logsDir, "Decks.log")
//        decksFile.createNewFile()
//        context.assets.open("Decks.log").reader()
//            .apply {
//                decksFile.writeText(readText())
//            }

        val powerFile = File(logsDir, "Power.log")
        powerFile.createNewFile()

        return context.findHSDataFilesDir("Logs")
    }
}