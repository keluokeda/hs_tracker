package com.ke.hs_tracker.module.ui.summary

import com.ke.hs_tracker.module.db.GameDao
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetHeroBattleRateListUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val gameDao: GameDao
) : UseCase<Unit, List<BattleRateItem.ClassBattleRate>>(dispatcher) {

    override suspend fun execute(parameters: Unit): List<BattleRateItem.ClassBattleRate> {

        val list = mutableListOf<BattleRateItem.ClassBattleRate>()

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
                val firstHandCount = pair.second.count {
                    it.isUserFirst == true
                }

                val item = BattleRateItem.ClassBattleRate(
                    heroWinCount,
                    heroLostCount,
                    firstHandCount,
                    pair.first
                )
                list.add(item)
            }

        return list
    }
}