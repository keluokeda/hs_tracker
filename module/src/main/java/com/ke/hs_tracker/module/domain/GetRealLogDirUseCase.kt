package com.ke.hs_tracker.module.domain

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.findHSDataFilesDir
import com.ke.hs_tracker.module.log
import com.ke.mvvm.base.domian.UseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetRealLogDirUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) :
    UseCase<Unit, DocumentFile?>(dispatcher) {

//    private var documentFile: DocumentFile? = null

    override suspend fun execute(parameters: Unit): DocumentFile? {

//        if (documentFile != null) {
//            return documentFile
//        }

        val logsDir = context.findHSDataFilesDir("Logs")
            ?: return null

        val listFiles = logsDir.listFiles()



        return listFiles.filter {
            "${it.name} ${it.lastModified()}".log()

            (it.name?.startsWith("Hearthstone") ?: false) && it.isDirectory
        }.maxByOrNull {
            it.lastModified()
        }?.apply {
            "找到了目标目录 ${this.name} ${this.lastModified()}".log()
        }


    }
}