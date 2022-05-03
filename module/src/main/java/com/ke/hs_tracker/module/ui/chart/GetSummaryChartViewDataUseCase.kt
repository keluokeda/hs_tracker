package com.ke.hs_tracker.module.ui.chart

import com.ke.hs_tracker.module.db.GameDao
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

internal class GetSummaryChartViewDataUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val gameDao: GameDao
) : UseCase<Unit, SummaryChartViewData>(dispatcher) {

    override suspend fun execute(parameters: Unit): SummaryChartViewData {
        val games = gameDao.getAll()
        val winCount = games.count {
            it.isUserWin == true
        }
        val lossCount = games.count {
            it.isUserWin == false
        }
        val firstHandCount = games.count {
            it.isUserFirst == true
        }
        val secondHandCount = games.count {
            it.isUserFirst == false
        }

        val firstHandWinCount = games.count {
            it.isUserFirst == true && it.isUserWin == true
        }

        val firstHandLossCount = games.count {
            it.isUserFirst == true && it.isUserWin == false
        }
        val secondHandWinCount = games.count {
            it.isUserFirst == false && it.isUserWin == true
        }

        val secondHandLossCount = games.count {
            it.isUserFirst == false && it.isUserWin == false
        }

        val classCounts = CardClass.values()
            .filter {
                it.isHero
            }.map {
                it to games.count { game ->
                    game.opponentHero == it
                }
            }

        return SummaryChartViewData(
            winCount,
            lossCount,
            firstHandCount,
            secondHandCount,
            firstHandWinCount,
            firstHandLossCount,
            secondHandWinCount,
            secondHandLossCount,
            classCounts
        )
    }
}