package com.ke.hs_tracker.module.ui.summary

import com.ke.hs_tracker.module.db.GameDao
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetDeckBattleRateListUseCase @Inject constructor(
    private val gameDao: GameDao,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, List<BattleRateItem.DeckBattleRate>>(dispatcher) {

    override suspend fun execute(parameters: Unit): List<BattleRateItem.DeckBattleRate> {
        val games = gameDao.getAll()
        return games
            .filter {
                it.userDeckName.isNotEmpty() && it.userDeckCode.isNotEmpty()
            }
            .groupBy {
                it.userDeckName to it.userDeckCode
            }.map { map ->
                BattleRateItem.DeckBattleRate(
                    map.value.count {
                        it.isUserWin == true
                    },
                    map.value.count {
                        it.isUserWin == false
                    },
                    map.value.count {
                        it.isUserFirst == false
                    },
                    map.key.first,
                    map.key.second
                )

            }

    }
}