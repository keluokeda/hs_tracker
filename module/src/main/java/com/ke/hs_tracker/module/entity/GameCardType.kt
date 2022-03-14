package com.ke.hs_tracker.module.entity

/**
 * 卡牌类型
 */
enum class GameCardType {

    Game,

    /**
     * 玩家
     */
    Player,

    /**
     * 英雄
     */
    Hero,

    /**
     * 英雄技能
     */
    HeroPower,


    /**
     * 牌库中的牌的状态
     */
    Invalid,

    /**
     * 随从身上的buff或战场上的buff（例如下一张法强怪法力值减少1）
     */
    Enchantment,

    /**
     * 法术
     */
    Spell,

    /**
     * 随从
     */
    Minion
}

/**
 * 字符串转 CardType类型
 */
internal fun String.toCardType(fallback: GameCardType = GameCardType.Game): GameCardType {
    return GameCardType.values().find {
        it.name.equals(this.replace("_", ""), true)
    } ?: fallback
}