package com.ke.hs.simulator.cards.base

interface HeroCard : ICard {
    /**
     * 护甲
     */
    val armor: Int

    var health: Int
}