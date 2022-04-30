package com.ke.hs_tracker.module.domain

import com.ke.hs_tracker.module.data.PreferenceStorage
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SetSaveLogFileEnableUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) :
    UseCase<Boolean, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Boolean): Boolean {
        preferenceStorage.saveLogFile = parameters
        return parameters
    }
}