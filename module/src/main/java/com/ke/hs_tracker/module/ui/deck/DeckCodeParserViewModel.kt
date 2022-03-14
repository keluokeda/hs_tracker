package com.ke.hs_tracker.module.ui.deck

import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.domain.ParseDeckCodeUseCase
import com.ke.hs_tracker.module.entity.CardBean
import com.ke.mvvm.base.data.successOr
import com.ke.mvvm.base.ui.BaseContentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckCodeParserViewModel @Inject constructor(private val parseDeckCodeUseCase: ParseDeckCodeUseCase) :
    BaseContentViewModel<List<CardBean>>() {


    fun start(code: String) {
        viewModelScope.launch {
            showLoadingDialog("加载中")
            val list = parseDeckCodeUseCase(code).successOr(emptyList())
            dismissLoadingDialog()
            showContent(list)
//            when (result) {
//                is Result.Success -> {
//                    showContent(result.data)
//                }
//                is Result.Error -> {
//                    result.exception.printStackTrace()
//                }
//            }
        }
    }
}