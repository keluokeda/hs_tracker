package com.ke.hs_tracker.module.entity

import kotlin.random.Random

data class CardBean(
    val card: Card,
    var count: Int = 0
) {
    //防止在使用MutableStateFlow时无法更新数据
    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return Random.nextInt()
    }

    fun toCardList(): List<Card> {
        val list = mutableListOf<Card>()
        repeat(count) {
            list.add(card)
        }
        return list
    }
}