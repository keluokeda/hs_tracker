package com.ke.hs_tracker.module.parser


import android.os.Looper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.InputStream

/**
 * power文件观察者
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

        delay(interval)


        while (true) {


            fileInputStreamProvider()
                ?.reader()
                ?.apply {
                    if (oldLogSize > 0) {
                        skip(oldLogSize)
                    }

                    val text = readText()
//                        try {
//                        readText()
//                    } catch (error: Throwable) {
//                        if (BuildConfig.DEBUG) {
//                            error.printStackTrace()
//                        }
//                        val size = fileInputStreamProvider()?.available() ?: 0
//                        Logger.d("内存溢出了 ，文件大小 $size,当前oldLogSize = $oldLogSize")
//                        oldLogSize += size
//
//                        readLines()
//                        ""
//                    }
//                    readLines()
//                    readTextFromFile()

                    oldLogSize += text.length
                    val lines = text.lines()
                        .filter {
                            it.startsWith("D", true)
                        }


                    emit(lines)

                    close()
                }
            delay(interval)
        }
    }


}


