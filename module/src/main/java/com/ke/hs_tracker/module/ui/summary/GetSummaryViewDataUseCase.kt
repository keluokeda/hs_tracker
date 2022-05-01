package com.ke.hs_tracker.module.ui.summary

import com.ke.hs_tracker.module.db.GameDao
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

internal class GetSummaryViewDataUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val gameDao: GameDao
) : UseCase<Unit, SummaryViewData>(dispatcher) {

    override suspend fun execute(parameters: Unit): SummaryViewData {


        var winCount = 0
        var lostCount = 0

        val list = mutableListOf<HeroBattleItem>()

        CardClass.values()
            .filter {
                it.isHero
            }
            .map {
                it to gameDao.getByHero(it)
            }.forEach { pair ->

                val heroWinCount = pair.second.count {
                    it.isUserWin == true
                }
                val heroLostCount = pair.second.count {
                    it.isUserWin == false
                }
                winCount += heroWinCount
                lostCount += heroLostCount
                val item = HeroBattleItem(
                    pair.first,
                    heroWinCount,
                    heroLostCount,
                    if (heroWinCount == 0) 0 else heroWinCount * 100 / (heroLostCount + heroWinCount)
                )
                list.add(item)
            }

        return SummaryViewData(
            winCount,
            lostCount,
            if (winCount == 0) 0 else winCount * 100 / (winCount + lostCount),
            list.sortedByDescending {
                it.rate
            }
        )
    }
}