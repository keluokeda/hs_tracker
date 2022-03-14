package com.ke.hs_tracker.core.entity


enum class Zone {
    /**
     * 战场
     */
    Play,

    /**
     * 牌库
     */
    Deck,

    /**
     * 发现的牌的位置
     */
    SetAside,

    /**
     * 墓地 打出的法术牌和死亡的随从会进入
     */
    Graveyard,

    /**
     * 手牌
     */
    Hand
}

internal fun String.toZone(fallback: Zone = Zone.Deck): Zone {
    return Zone.values().firstOrNull {
        it.name.equals(this, true)
    } ?: fallback
}