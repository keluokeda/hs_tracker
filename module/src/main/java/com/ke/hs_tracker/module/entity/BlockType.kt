package com.ke.hs_tracker.module.entity

enum class BlockType {
    /**
     * 攻击
     */
    Attack,

    /**
     * 死亡
     */
    Deaths,

    /**
     * 触发
     */
    Trigger,

    /**
     * 打出一张卡牌
     */
    Play,

    /**
     * 卡牌生效
     */
    Power,

    /**
     * 交易
     */
    Trade

}

internal fun String.toBlockType(fallback: BlockType = BlockType.Trigger): BlockType {

    BlockType.values().forEach {
        if (it.name.equals(this, true)) {
            return it
        }
    }

    return fallback
}