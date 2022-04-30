package com.ke.hs_tracker.module.entity


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
    Hand,

    /**
     * 位置
     */
    Unknown,

    /**
     * 衍生牌被消耗后会去到这个地方
     */
    //TAG_CHANGE Entity=[entityName=研习符文 id=98 zone=PLAY zonePos=0 cardId=SCH_270e2 player=2] tag=1068 value=5
    //TAG_CHANGE Entity=[entityName=研习符文 id=98 zone=PLAY zonePos=0 cardId=SCH_270e2 player=2] tag=1068 value=0
    //TAG_CHANGE Entity=[entityName=研习符文 id=98 zone=PLAY zonePos=0 cardId=SCH_270e2 player=2] tag=ZONE value=REMOVEDFROMGAME
    //TAG_CHANGE Entity=[entityName=研习符文 id=98 zone=PLAY zonePos=0 cardId=SCH_270e2 player=2] tag=1234 value=94
    RemovedFromGame,

    /**
     * 奥秘
     */
    Secret
}

internal fun String.toZone(fallback: Zone = Zone.Deck): Zone {
    return Zone.values().firstOrNull {
        it.name.equals(this, true)
    } ?: fallback
}