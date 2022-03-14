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

class GetRealLogDirUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) :
    UseCase<Unit, DocumentFile?>(dispatcher) {

    private var documentFile: DocumentFile? = null

    override suspend fun execute(parameters: Unit): DocumentFile? {

        if (documentFile != null) {
            return documentFile
        }

        return context.findHSDataFilesDir("Logs")?.apply {
            documentFile = this
        }
    }
}