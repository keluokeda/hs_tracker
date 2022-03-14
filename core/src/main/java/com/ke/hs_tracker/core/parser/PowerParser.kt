package com.ke.hs_tracker.core.parser

import com.ke.hs_tracker.core.entity.*
import com.ke.hs_tracker.core.entity.toCardType
import com.ke.hs_tracker.core.removeTime


/**
 * 日志解析
 */
interface PowerParser {
    /**
     * 解析一行日志
     */
    fun parse(content: String)

    /**
     * 解析结果监听
     */
    var powerTagListener: ((PowerTag) -> Unit)?
}

class PowerParserImpl : PowerParser {

    private val blockTagStack: BlockTagStack = BlockTagStackImpl()


    override var powerTagListener: ((PowerTag) -> Unit)? = null


    override fun parse(content: String) {

        val pair = checkTypeAndReturnContent(content) ?: return



        if (pair.first == LogType.PowerTaskList) {
            handlePowerTaskListLog(pair.second)
        } else {
            handleGameStateLog(pair.second)
        }

    }


    /**
     * 检查日志类型并返回去掉时间和日期前缀的内容
     */
    private fun checkTypeAndReturnContent(content: String): Pair<LogType, String>? {
        if (content.length < TIME_PREFIX_SIZE) {
            return null
        }
        val noTimeContent = content.substring(TIME_PREFIX_SIZE)
        if (noTimeContent.startsWith(LogType.GameState.replace)) {
            return LogType.GameState to noTimeContent.replace(LogType.GameState.replace, "").trim()
        } else if (noTimeContent.startsWith(LogType.PowerTaskList.replace)) {
            return LogType.PowerTaskList to noTimeContent.replace(LogType.PowerTaskList.replace, "")
                .trim()
        }

        return null
    }

    private fun handlePowerTaskListLog(line: String) {

        val result = blockTagStack.insert(line)
        when (result) {
            InsertStackResult.CanNotInsert -> {
                handleUnSupportNestedTag(line)
            }
            is InsertStackResult.Over -> {
                powerTagListener?.invoke(result.powerTag)
                if (!result.handled) {
                    //需要自己处理
                    handleUnSupportNestedTag(line)
                }
            }
            InsertStackResult.Success -> {

            }
        }

    }

    private fun handleUnSupportNestedTag(line: String) {
        var matchResult = TAG_CHANGE_PATTERN.matchEntire(line)
        if (matchResult != null) {
            handleTagChangeLine(
                matchResult.groupValues[1],
                matchResult.groupValues[2],
                matchResult.groupValues[3]
            )
        }
    }

    private fun handleTagChangeLine(content: String, tag: String, value: String) {

        val entity = Entity.createFromContent(content)!!

        powerTagListener?.invoke(PowerTag.PowerTaskList.TagChange(entity, tag, value))
    }

    private fun handleGameStateLog(content: String) {


        BUILD_NUMBER_PATTERN.matchEntire(content)?.apply {
            val tag = PowerTag.GameState.BuildNumber(
                groupValues[1]
            )
            powerTagListener?.invoke(tag)
            return
        }

        GAME_TYPE_PATTERN.matchEntire(content)?.apply {
            val tag = PowerTag.GameState.GameType(
                groupValues[1]
            )
            powerTagListener?.invoke(tag)
            return
        }
        FORMAT_TYPE_PATTERN.matchEntire(content)?.apply {
            val tag = PowerTag.GameState.FormatType(
                groupValues[1]
            )

            powerTagListener?.invoke(tag)
            return
        }

        SCENARIO_ID_PATTERN.matchEntire(content)?.apply {
            val tag = PowerTag.GameState.ScenarioID(
                groupValues[1]
            )
            powerTagListener?.invoke(tag)
            return
        }
        PLAYER_MAPPING_PATTERN.matchEntire(content)?.apply {
            val tag = PowerTag.GameState.PlayerMapping(
                groupValues[1].toInt(), groupValues[2]
            )
            powerTagListener?.invoke(tag)
            return
        }

    }


    companion object {
        const val TIME_PREFIX_SIZE = 19

        //CREATE_GAME
        internal val CREATE_GAME_PATTERN = Regex("CREATE_GAME")

        //BuildNumber=127581
        internal val BUILD_NUMBER_PATTERN = Regex("BuildNumber=(.*)")

        //GameType=GT_CASUAL
        internal val GAME_TYPE_PATTERN = Regex("GameType=(.*)")

        //FormatType=FT_WILD
        internal val FORMAT_TYPE_PATTERN = Regex("FormatType=(.*)")

        //ScenarioID=2
        internal val SCENARIO_ID_PATTERN = Regex("ScenarioID=(.*)")

        //PlayerID=2, PlayerName=阿克萌德#51240
        internal val PLAYER_MAPPING_PATTERN = Regex("PlayerID=(.*), PlayerName=(.*)")

        // tag=CARDTYPE value=GAME
        internal val TAG_PATTERN = Regex("tag=(.*) value=(.*)")

        //TAG_CHANGE Entity=GameEntity tag=STATE value=RUNNING
        internal val TAG_CHANGE_PATTERN = Regex("TAG_CHANGE Entity=(.*) tag=(.*) value=(.*)")


        //GameEntity EntityID=1
        internal val GAME_ENTITY_PATTERN = Regex("GameEntity EntityID=(.*)")

        //FULL_ENTITY - Updating [entityName=加尔鲁什·地狱咆哮 id=64 zone=PLAY zonePos=0 cardId=HERO_01 player=1] CardID=HERO_01
        internal val FULL_ENTITY_PATTERN = Regex("FULL_ENTITY - Updating (.*) CardID=(.*)")

        //[entityName=UNKNOWN ENTITY [cardType=INVALID] id=83 zone=DECK zonePos=0 cardId= player=2]
        val FULL_ENTITY_CONTENT1_PATTERN =
            Regex("\\[entityName=(.*) \\[cardType=(.*)] id=(.*) zone=(.*) zonePos=(.*) cardId=(.*) player=(.*)]")

        val FULL_ENTITY_CONTENT2_PATTERN =
            Regex("\\[entityName=(.*) id=(.*) zone=(.*) zonePos=(.*) cardId=(.*) player=(.*)]")

        val SHOW_ENTITY = Regex("SHOW_ENTITY - Updating Entity=(.*) CardID=(.*)")


        //Player EntityID=2 PlayerID=1 GameAccountId=[hi=144115211015832391 lo=191215280]
        internal val PLAYER_ENTITY_PATTERN =
            Regex("Player EntityID=(.*) PlayerID=(.*) GameAccountId=(.*)")

        //BLOCK_START BlockType=ATTACK Entity=[entityName=瓦丝琪女士 id=87 zone=PLAY zonePos=1 cardId=BT_109 player=2]
        // EffectCardId=System.Collections.Generic.List`1[System.String]
        // EffectIndex=0 Target=[entityName=驯化的雷象 id=78 zone=PLAY zonePos=1 cardId=SCH_714 player=1] SubOption=-1
        internal val BLOCK_START_PATTERN =
            Regex("BLOCK_START BlockType=(.*) Entity=(.*) EffectCardId=(.*) EffectIndex=(.*) Target=(.*) SubOption=(.*)")

        internal val BLOCK_START_CONTINUATION_PATTERN = Regex("(.*) TriggerKeyword=(.*)")

        //BLOCK_END
        internal val BLOCK_END_PATTERN = Regex("BLOCK_END")

    }
}