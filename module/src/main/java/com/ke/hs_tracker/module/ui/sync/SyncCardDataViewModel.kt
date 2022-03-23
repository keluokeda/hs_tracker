package com.ke.hs_tracker.module.ui.sync

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.domain.ClearCardTableUseCase
import com.ke.hs_tracker.module.domain.GetCardListUseCase
import com.ke.hs_tracker.module.domain.InsertCardListToDatabaseUseCase
import com.ke.mvvm.base.data.Result
import com.ke.mvvm.base.model.SnackbarAction
import com.ke.mvvm.base.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncCardDataViewModel @Inject constructor(
    private val getCardListUseCase: GetCardListUseCase,
    private val clearCardTableUseCase: ClearCardTableUseCase,
    private val insertCardListToDatabaseUseCase: InsertCardListToDatabaseUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    internal val showBackButton =
        savedStateHandle.get<Boolean>(SyncCardDataActivity.EXTRA_SHOW_BACK_BUTTON) ?: false

    private val _navigationActions =
        Channel<SyncCardDataNavigationAction>(capacity = Channel.CONFLATED)

    val navigationActions: Flow<SyncCardDataNavigationAction>
        get() = _navigationActions.receiveAsFlow()

    fun sync(
        versionCode: String,
        region: String
    ) {

        viewModelScope.launch {
            showLoadingDialog("同步中")

            val code = versionCode.ifEmpty { "latest" }

            when (val result = getCardListUseCase(code to region)) {
                is Result.Success -> {
                    clearCardTableUseCase(Unit)
                    insertCardListToDatabaseUseCase(result.data)
                    dismissLoadingDialog()
                    _navigationActions.send(if (showBackButton) SyncCardDataNavigationAction.NavigateToBack else SyncCardDataNavigationAction.NavigateToMain)
                }
                is Result.Error -> {
                    result.exception.printStackTrace()
                    dismissLoadingDialog()
                    showSnackbar(SnackbarAction("从服务器获取数据失败"))
//                    throw result.exception
                }
            }

        }
    }
}

sealed interface SyncCardDataNavigationAction {
    /**
     * 去首页
     */
    object NavigateToMain : SyncCardDataNavigationAction

    /**
     * 返回上一个页面
     */
    object NavigateToBack : SyncCardDataNavigationAction
}
