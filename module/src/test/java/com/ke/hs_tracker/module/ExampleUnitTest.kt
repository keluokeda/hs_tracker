package com.ke.hs_tracker.module

import com.ke.hs_tracker.module.db.CardClassesConvert
import com.ke.hs_tracker.module.entity.CardClass
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testCardClassesConvert() {
        val convert = CardClassesConvert()

        val source = listOf(
            CardClass.Hunter,
            CardClass.Druid,
            CardClass.Mage
        )

        val l = convert.classesToLong(source)

        val list = convert.longToClasses(l)
        val size = list.size

        assertEquals(source, list)
    }
}