package com.ke.hs_tracker.module.ui.permissions

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.domain.GetDatabaseCardCountUseCase
import com.ke.hs_tracker.module.domain.WriteLogConfigFileUseCase
import com.ke.mvvm.base.data.successOr
import com.ke.mvvm.base.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val getDatabaseCardCountUseCase: GetDatabaseCardCountUseCase,
    private val writeLogConfigFileUseCase: WriteLogConfigFileUseCase
) : BaseViewModel() {

    private val _navigationActions =
        Channel<PermissionsNavigationAction>(capacity = Channel.CONFLATED)

    val navigationActions: Flow<PermissionsNavigationAction>
        get() = _navigationActions.receiveAsFlow()

    internal fun next() {

        viewModelScope.launch {
            _navigationActions.send(PermissionsNavigationAction.NavigateToWriteConfig)
//            writeLogConfigFileUseCase(true)
//            if (getDatabaseCardCountUseCase(Unit).successOr(0) == 0) {
//                _navigationActions.send(PermissionsNavigationAction.NavigateToSync)
//            } else {
//                _navigationActions.send(PermissionsNavigationAction.NavigateToMain)
//            }
        }
    }
}

sealed interface PermissionsNavigationAction {
//    data class ShowErrorDialog(
//        val message: String
//    ) : PermissionsNavigationAction

//    object NavigateToMain : PermissionsNavigationAction

    object NavigateToWriteConfig : PermissionsNavigationAction
}