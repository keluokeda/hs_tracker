package com.ke.hs_tracker.module.parser


import android.os.Looper
import com.ke.hs_tracker.module.entity.CurrentDeck
import com.ke.hs_tracker.module.removeTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.InputStream

/**
 * deck文件观察者
 */
class DeckFileObserver constructor(
    private val interval: Long = 2000,
    private val deckFileInputStreamProvider: suspend () -> InputStream?,
) {
    private var oldLogSize = 0L


    fun reset() {
        oldLogSize = 0
    }

    suspend fun start(): Flow<CurrentDeck> = flow {

        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            throw RuntimeException("不能运行在主线程")
        }

        while (true) {
            deckFileInputStreamProvider()?.reader()?.apply {
                if (oldLogSize > 0) {
                    skip(oldLogSize)
                }
                val text = readText()
                oldLogSize += text.length
                val lines = text.lines()
                    .filter { it.isNotEmpty() }

                listToDeck(lines)?.apply {
                    emit(this)
                }
                close()
            }
            delay(interval)
        }
    }

    private fun listToDeck(list: List<String>): CurrentDeck? {
        if (list.isEmpty()) {
            return null
        }
        val contentList = list.map {
            it.removeTime().third
        }
        val name = contentList.findLast {
            it.startsWith("###", true)
        } ?: return null

        val target =
            contentList.subList(contentList.indexOf(name), contentList.size).toMutableList()
        target.removeFirst()
        target.removeFirst()
        val code = target.removeFirst()

        return CurrentDeck(
            name.replace("### ", ""), code
        )
    }
}
