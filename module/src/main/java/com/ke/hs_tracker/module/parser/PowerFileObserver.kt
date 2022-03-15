package com.ke.hs_tracker.module.parser


import android.os.Looper
import com.ke.hs_tracker.module.log
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

    fun reset() {
        oldLogSize = 0
    }

    suspend fun start(): Flow<List<String>> = flow {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            throw RuntimeException("不能运行在主线程")
        }

        while (true) {


            fileInputStreamProvider()?.reader()?.apply {
                val start = System.currentTimeMillis()
                if (oldLogSize > 0) {
                    skip(oldLogSize)
                }
                val text =
//                    readLines()
                    readText()

                oldLogSize += text.length
                val lines = text.lines()
                    .filter {
                        it.startsWith("D", true)
                    }
//                    .filter { it.isNotEmpty() }
//                oldLogSize += if (text.isEmpty()) 0 else text.map { it.length }
//                    .reduce { acc, i -> acc + i }

                "本次读取花费时间 = ${System.currentTimeMillis() - start} ，处理的数据 = $lines".log()

                emit(lines)

                close()
            }
            delay(interval)
        }
    }


}
