package com.ke.hs_tracker.module.ui.deckbattledetail

import com.ke.hs_tracker.module.db.Game
import com.ke.hs_tracker.module.db.GameDao
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetGamesByDeckCodeAndNameUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val gameDao: GameDao
) : UseCase<Pair<String, String>, List<Game>>(dispatcher) {

    override suspend fun execute(parameters: Pair<String, String>): List<Game> {

        return gameDao.getByDeckCodeAndName(
            parameters.first,
            parameters.second
        )
    }
}