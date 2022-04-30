package com.ke.hs_tracker.module.domain

import android.content.Context
import android.net.Uri
import com.ke.hs_tracker.module.data.PreferenceStorage
import com.ke.mvvm.base.domian.UseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class SaveLogFileUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @ApplicationContext private val context: Context
) :
    UseCase<Pair<String, InputStream>, Boolean>(Dispatchers.IO) {
    override suspend fun execute(parameters: Pair<String, InputStream>): Boolean {
        if (!preferenceStorage.saveLogFile) {
            return false
        }
        val targetFileDir = File(context.getExternalFilesDir(null), "logs")
        if (!targetFileDir.exists()) {
            targetFileDir.mkdir()
        }
        val target = File(targetFileDir, parameters.first + ".log")
        if (!target.exists()) {
            target.createNewFile()
        }
        val text = parameters.second.bufferedReader().readText()

        target.writeText(text)

        return true
    }
}