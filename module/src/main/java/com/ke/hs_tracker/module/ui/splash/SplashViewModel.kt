package com.ke.hs_tracker.module.ui.splash

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.domain.GetDatabaseCardCountUseCase
import com.ke.hs_tracker.module.hasAllPermissions
import com.ke.mvvm.base.data.successOr
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getDatabaseCardCountUseCase: GetDatabaseCardCountUseCase
) :
    ViewModel() {

    private val _navigationActions = Channel<SplashNavigationAction>(capacity = Channel.CONFLATED)

    val navigationActions: Flow<SplashNavigationAction>
        get() = _navigationActions.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (context.hasAllPermissions) {

                if (getDatabaseCardCountUseCase(Unit).successOr(0) == 0) {
                    _navigationActions.send(SplashNavigationAction.NavigateToSync)
                } else {
                    _navigationActions.send(SplashNavigationAction.NavigateToMain)
                }
            } else {
                _navigationActions.send(SplashNavigationAction.NavigateToPermissions)
            }
        }


    }
}

sealed interface SplashNavigationAction {
    object NavigateToPermissions : SplashNavigationAction

    object NavigateToMain : SplashNavigationAction

    object NavigateToSync : SplashNavigationAction
}