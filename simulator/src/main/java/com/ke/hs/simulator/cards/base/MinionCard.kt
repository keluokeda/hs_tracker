package com.ke.hs.simulator.cards.base

import com.ke.hs_tracker.module.entity.Race

interface MinionCard : ICard {

    /**
     * 攻击力
     */
    var attack: Int

    /**
     * 生命值
     */
    var health: Int

    /**
     * 种族
     */
    val race: Race?
}