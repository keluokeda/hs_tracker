package com.ke.hs_tracker.core.parser

import com.ke.hs_tracker.core.entity.CurrentDeck
import com.ke.hs_tracker.core.entity.PowerTag
import com.ke.hs_tracker.core.removeTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.InputStream

/**
 * deck文件观察者
 */
class PowerFileObserver(
    private val interval: Long = 2000,
    private val fileInputStreamProvider: suspend () -> InputStream?,
) {
    private var oldLogSize = 0L

    fun reset(){
        oldLogSize = 0
    }

    suspend fun start(): Flow<List<String>> = flow {


        while (true) {
            fileInputStreamProvider()?.reader()?.apply {
                if (oldLogSize > 0) {
                    skip(oldLogSize)
                }
                val text = readText()
                oldLogSize += text.length
                val lines = text.lines()
                    .filter { it.isNotEmpty() }

                emit(lines)

                close()
            }
            delay(interval)
        }
    }


}
