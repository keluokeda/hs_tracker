package com.ke.hs_tracker.module.ui.records

import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.db.Game
import com.ke.hs_tracker.module.db.GameDao
import com.ke.mvvm.base.ui.BaseContentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor(private val gameDao: GameDao) :
    BaseContentViewModel<List<Game>>() {


    init {
        loadData()
    }

    internal fun loadData() {

        viewModelScope.launch {
            showLoading()
            showContent(gameDao.getAll().filter {
                //掉线问题
                it.opponentHero != null && it.isUserFirst != null
            })
        }
    }
}