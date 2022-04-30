package com.ke.hs_tracker.module.domain

import com.ke.hs_tracker.module.data.PreferenceStorage
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetSaveLogFileEnableUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) :
    UseCase<Unit, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Unit): Boolean {
        return preferenceStorage.saveLogFile
    }
}