package com.ke.hs_tracker.module.entity

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * 类型
 */

enum class Mechanics {

    /**
     * 注能
     */
    Infuse,

    /**
     * 过载
     */
    Overload,


    /**
     * 战吼
     */
    BattleCry,

    /**
     * 光环
     */
    Aura,

    /**
     * 冲锋
     */
    Charge,

    /**
     * 嘲讽
     */
    Taunt,

    /**
     * 突袭
     */
    Rush,

    /**
     * 巨型
     */
    Colossal,

    /**
     * 探底
     */
    Dredge,

    /**
     * 潜行
     */
    Stealth,

    /**
     * 圣盾
     */
    DivineShield,

    /**
     * 法强
     */
    SpellPower,

    /**
     * 抽到的时候
     */
    TopDeck,

    /**
     * 亡语
     */
    DeathRattle,

    /**
     * 秘密选择
     */
    Counter,

    InvisibleDeathRattle,

    /**
     * 变形
     */
    Morph,

    /**
     * 激怒
     */
    Enraged,

    /**
     * 50%几率攻击错误的敌人
     */
    Forgetful,

    /**
     * 本回合生效
     */
    TagOneTurnEffect,

    /**
     * 抉择
     */
    ChooseOne,

    /**
     * 荣誉击杀
     */
    HonorableKill,

    /**
     * 法力迸发
     */
    SpellBurst,

    /**
     * 腐蚀
     */
    Corrupt,

    /**
     * 暴怒
     */
    Frenzy,

    /**
     * 发现
     */
    Discover,

    /**
     * 冻结
     */
    Freeze,

    /**
     * 连击
     */
    Combo,

    /**
     * 无法攻击
     */
    CantAttack,

    /**
     * 触发
     */
    TriggerVisual,

    /**
     * 奥秘
     */
    Secret,

    /**
     * 任务
     */
    Quest,

    /**
     * 克苏恩
     */
    Ritual,

    /**
     * 交易
     */
    Tradeable,

    /**
     * 激励
     */
    Inspire,

    /**
     * 沉默
     */
    Silence,

    /**
     * 风怒
     */
    Windfury,

    /**
     * 回响
     */
    Echo,

    /**
     * 污手党
     */
    GrimyGoons,

    /**
     * 暗金教
     */
    Kabal,

    /**
     * 玉莲帮
     */
    JadeLotus,

    /**
     * 青玉魔像
     */
    JadeGolem,

    /**
     * 不可见的衍生牌
     */
    EnchantmentInvisible,

    /**
     * 英雄技能造成额外的伤害
     */
    HeroPowerDamage,

    /**
     * 回合结束时如果这张牌仍在手牌中，将其摧毁
     */
    Ghostly,

    /**
     * 受到法强翻倍
     */
    ReceivesDoubleSpellDamageBonus,

    /**
     * 零件
     */
    SparePart,

    AutoAttack,

    /**
     * 唤尸者专属
     */
    DeathKnight,

    /**
     * 偶数
     */
    CollectionmanagerFilterManaEven,

    /**
     * 奇数
     */
    CollectionmanagerFilterManaOdd,

    /**
     * 不受法强影响的法术
     */
    ImmuneToSpellPower,

    /**
     * 无法被沉默
     */
    CantBeSilenced,


    CantBeDestroyed,

    /**
     * 黑棋国王
     */
    CantBeFatigued,

    /**
     * 支线任务
     */
    SideQuest,

    /**
     * 双生法术
     */
    TwinSpell,

    /**
     * 剧毒
     */
    Poisonous,

    /**
     * 吸血
     */
    LifeSteal,

    /**
     * 流放
     */
    Outcast,

    /**
     * 不可接触的
     */
    Untouchable,

    /**
     * 复仇
     */
    Avenge,

    /**
     * 超杀
     */
    Overkill,

    /**
     * 复生
     */
    Reborn,

    AIMustPlay,

    /**
     * 免疫
     */
    Immune,

    /**
     * 无法成为法术的目标
     */
    CantBeTargetedBySpells,

    /**
     * 无法成为英雄技能的目标
     */
    CantBeTargetedByHeroPowers,

    /**
     * 磁力
     */
    Modular,

    /**
     * 如果这张牌在你的手牌中，在你的回合开始时，你的英雄受到2点伤害
     */
    EvilGlow,

    /**
     * 相邻的随从获得buff
     */
    AdjacentBuff,

    /**
     * 对局开始时
     */
    StartOfGame,

    /**
     * 在你攻击一个随从后，迫使其攻击相邻的一个随从
     */
    FinishAttackSpellOnDamage,


    AppearFunctionallyDead,

    Gears,

    Puzzle,

    MultiplyBuffValue,

    AffectedBySpellPower,


    DungeonPassiveBuff,

    IgnoreHideStatsForBigCard,

    Summoned,

    /**
     * 法力渴求
     */
    ManaThirst,

    OVERHEAL,

    VENOMOUS,

    MAGNETIC,

    FORGE,

    TITAN
}


class MechanicsAdapter {

    @FromJson
    fun fromJson(value: String): Mechanics {

        return EnumMoshiAdapter.fromJson(value, Mechanics.values())

    }

    @ToJson
    fun toJson(mechanics: Mechanics) = EnumMoshiAdapter.toJson(mechanics)
}