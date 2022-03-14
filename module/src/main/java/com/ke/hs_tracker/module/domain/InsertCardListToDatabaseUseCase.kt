package com.ke.hs_tracker.module.domain

import com.ke.hs_tracker.module.db.CardDao
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.entity.Card
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class InsertCardListToDatabaseUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val cardDao: CardDao
) : UseCase<List<Card>, Unit>(dispatcher) {
    override suspend fun execute(parameters: List<Card>) {
        cardDao.insert(parameters)
    }
}