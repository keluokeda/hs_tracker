package com.ke.hs_tracker.module.domain

import com.ke.hs_tracker.module.db.CardDao
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ClearCardTableUseCase @Inject constructor(
    private val cardDao: CardDao,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, Unit>(dispatcher) {
    override suspend fun execute(parameters: Unit) {
        cardDao.deleteAll()
    }
}