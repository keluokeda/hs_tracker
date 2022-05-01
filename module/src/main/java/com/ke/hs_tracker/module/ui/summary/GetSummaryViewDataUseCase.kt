package com.ke.hs_tracker.module.ui.summary

import com.ke.hs_tracker.module.db.GameDao
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

internal class GetSummaryViewDataUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val gameDao: GameDao
) : UseCase<Unit, SummaryViewData>(dispatcher) {

    override suspend fun execute(parameters: Unit): SummaryViewData {


        val winCount = gameDao.getUserWinCount()
        val allCount = gameDao.getGameCount()
        val lostCount = allCount - winCount



        return SummaryViewData(
            winCount,
            lostCount,
            if (winCount == 0) 0 else winCount * 100 / (winCount + lostCount),
        )
    }
}