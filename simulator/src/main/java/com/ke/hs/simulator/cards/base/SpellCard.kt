package com.ke.hs.simulator.cards.base

import com.ke.hs_tracker.module.entity.SpellSchool

interface SpellCard : ICard {

    /**
     * 法术类型
     */
    val spellSchool: SpellSchool?
}