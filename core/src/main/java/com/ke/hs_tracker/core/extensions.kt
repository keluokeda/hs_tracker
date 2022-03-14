package com.ke.hs_tracker.core

import com.ke.hs_tracker.core.parser.PowerParserImpl
import java.util.*


fun String.removeTime(): Triple<String, Date, String> {

    val content = substring(PowerParserImpl.TIME_PREFIX_SIZE)
    val start = substring(0, 1)
    val hms = substring(2, 10).split(":")
    val calendar = Calendar.getInstance()
    calendar.set(
        Calendar.HOUR_OF_DAY, hms[0].toInt()
    )
    calendar.set(
        Calendar.MINUTE, hms[1].toInt()
    )
    calendar.set(
        Calendar.SECOND, hms[2].toInt()
    )

    return Triple(
        start,
        calendar.time,
        content
    )
}

fun main(){
    val text = "I 22:23:35.6401730 AAECAaoIBJzOA6beA8L2A9ySBA3buAPhzAPNzgPw1AOK5APq5wP67APk9gOF+gOogQSVkgT5nwT6nwQA"
    println(text.removeTime().toString())
}