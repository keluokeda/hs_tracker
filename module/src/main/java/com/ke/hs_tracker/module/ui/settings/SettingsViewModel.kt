package com.ke.hs_tracker.module.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.domain.GetSaveLogFileEnableUseCase
import com.ke.hs_tracker.module.domain.SetSaveLogFileEnableUseCase
import com.ke.mvvm.base.data.successOr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSaveLogFileEnableUseCase: GetSaveLogFileEnableUseCase,
    private val setSaveLogFileEnableUseCase: SetSaveLogFileEnableUseCase
) : ViewModel() {


    private val _saveLogFileEnable = MutableStateFlow(false)
    internal val saveLogFileEnable: StateFlow<Boolean>
        get() = _saveLogFileEnable

    init {

        viewModelScope.launch {
            _saveLogFileEnable.value = getSaveLogFileEnableUseCase(Unit).successOr(false)
        }
    }

    internal fun setSaveLogFileEnable(enable: Boolean) {
        viewModelScope.launch {
            _saveLogFileEnable.value = setSaveLogFileEnableUseCase(enable).successOr(enable)
        }
    }
}