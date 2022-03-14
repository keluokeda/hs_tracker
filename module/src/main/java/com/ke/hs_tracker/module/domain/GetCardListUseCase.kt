package com.ke.hs_tracker.module.domain

import com.ke.hs_tracker.module.api.HearthStoneJsonApi
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.entity.Card
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetCardListUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val api: HearthStoneJsonApi
) :
    UseCase<Pair<String, String>, List<Card>>(dispatcher) {

    override suspend fun execute(parameters: Pair<String, String>): List<Card> {
        return api.getCardJsonList(
            parameters.first,
            parameters.second
        )
    }
}