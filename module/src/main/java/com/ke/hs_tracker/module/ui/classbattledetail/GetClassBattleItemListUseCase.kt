package com.ke.hs_tracker.module.ui.classbattledetail

import com.ke.hs_tracker.module.db.GameDao
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

internal class GetClassBattleItemListUseCase @Inject constructor(
    @IoDispatcher
    dispatcher: CoroutineDispatcher,
    private val dao: GameDao
) : UseCase<CardClass, List<ClassBattleItem>>(dispatcher) {

    override suspend fun execute(parameters: CardClass): List<ClassBattleItem> {

        val items = mutableListOf<ClassBattleItem>()
        dao.getByHero(parameters)
            .groupBy {
                it.opponentHero!!
            }.forEach { cardClass, list ->
                val times = list.size
                val win = list.count {
                    it.isUserWin == true
                }
                val loss = times - win
                val rate = win * 100 / times
                val item = ClassBattleItem(cardClass, times, win, loss, rate)

                items.add(item)
            }

        return items
    }
}