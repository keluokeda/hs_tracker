package com.ke.hs_tracker.module.parser

import com.ke.hs_tracker.module.entity.*
import com.orhanobut.logger.Logger
import java.util.*
import javax.inject.Inject


interface BlockTagStack {

    /**
     * 插入一条日志
     */
    fun insert(line: String): InsertStackResult


}

class BlockTagStackImpl @Inject constructor() : BlockTagStack {

    private val nestedTagList = LinkedList<NestedTag>()


    override fun insert(line: String): InsertStackResult {

        //CREATE_GAME
        var matchResult = PowerParserImpl.CREATE_GAME_PATTERN.matchEntire(line)

        if (matchResult != null) {
            //游戏开始
            nestedTagList.clear()
            nestedTagList.add(NestedTag.CreateGame)
            return InsertStackResult.Success
        }

        //GameEntity EntityID=1
        matchResult = PowerParserImpl.GAME_ENTITY_PATTERN.matchEntire(line)
        if (matchResult != null) {
            val entityId = matchResult.groupValues[1].toInt()

            nestedTagList.add(NestedTag.GameEntity(entityId))
            return InsertStackResult.Success
        }
        //Player EntityID=2 PlayerID=1 GameAccountId=[hi=144115211015832391 lo=191215280]
        matchResult = PowerParserImpl.PLAYER_ENTITY_PATTERN.matchEntire(line)
        if (matchResult != null) {
            val entityId = matchResult.groupValues[1].toInt()
            val playerId = matchResult.groupValues[2].toInt()
            nestedTagList.add(NestedTag.Player(entityId, playerId))
            return InsertStackResult.Success
        }
        //tag=CARDTYPE value=GAME
        matchResult = PowerParserImpl.TAG_PATTERN.matchEntire(line)
        if (matchResult != null) {
            val key = matchResult.groupValues[1]
            val value = matchResult.groupValues[2]
            nestedTagList.add(NestedTag.Tag(key, value))
            return InsertStackResult.Success
        }

        //BLOCK_START
        // BlockType=TRIGGER
        // Entity=GameEntity
        // EffectCardId=System.Collections.Generic.List`1[System.String]
        // EffectIndex=-1
        // Target=0
        // SubOption=-1
        // TriggerKeyword=TAG_NOT_SET
        matchResult = PowerParserImpl.BLOCK_START_PATTERN.matchEntire(line)
        if (matchResult != null) {
            val blockType = matchResult.groupValues[1].toBlockType()
            val entity = Entity.createFromContent(matchResult.groupValues[2])!!
            val target = Entity.createFromContent(matchResult.groupValues[5])
            val block = NestedTag.Block(blockType, entity, target)
            nestedTagList.add(block)
            return InsertStackResult.Success
        }

        matchResult = PowerParserImpl.BLOCK_END_PATTERN.matchEntire(line)
        if (matchResult != null) {
            //块结束了
            if (nestedTagList.isEmpty()) {
                Logger.d("准备插入一个end到空的列表里面")
                return InsertStackResult.Success
            }

            nestedTagList.add(NestedTag.BlockEnd)
//            val blockStartList = nestedTagList.filterIsInstance<NestedTag.Block>()
            val blockCount = nestedTagList.count {
                it is NestedTag.Block
            }
            val blockEndCount = nestedTagList.count { it is NestedTag.BlockEnd }

            if (blockCount == blockEndCount) {
                return InsertStackResult.Over(flushBlock(nestedTagList), true)
            } else {
                return InsertStackResult.Success
            }

        }

        //TAG_CHANGE Entity=阿克萌德#51240 tag=CURRENT_PLAYER value=1
        //TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=29 zone=DECK zonePos=0 cardId= player=1] tag=ZONE_POSITION value=1
        matchResult = PowerParserImpl.TAG_CHANGE_PATTERN.matchEntire(line)
        if (matchResult != null) {
            return when (val first = nestedTagList.firstOrNull()) {

                is NestedTag.FullEntity -> {
                    val powerTag = flushFullEntityWhenFirst()
                    //处理堆栈
                    InsertStackResult.Over(powerTag, false)
                }
                is NestedTag.ShowEntity -> {

                    val showEntity = PowerTag.PowerTaskList.ShowEntity(
                        first.entity,
                        first.cardId
                    )
                    nestedTagList.forEach {
                        if (it is NestedTag.TagChange) {
                            showEntity.payloads[it.tag] = it.value
                        }
                    }
                    nestedTagList.clear()
                    InsertStackResult.Over(showEntity, false)
                }
                is NestedTag.Block -> {
                    val tagChange = NestedTag.TagChange(
                        Entity.createFromContent(matchResult.groupValues[1])!!,
                        matchResult.groupValues[2],
                        matchResult.groupValues[3],
                    )
                    nestedTagList.add(tagChange)
                    InsertStackResult.Success
                }
                else -> {
                    InsertStackResult.CanNotInsert
                }
            }
        }

        //FULL_ENTITY - Updating [entityName=UNKNOWN ENTITY [cardType=INVALID] id=4 zone=DECK zonePos=0 cardId= player=1] CardID=
        matchResult = PowerParserImpl.FULL_ENTITY_PATTERN.matchEntire(line)
        if (matchResult != null) {
            val first = nestedTagList.firstOrNull()
            if (first is NestedTag.CreateGame) {
                //create game 接 full entity
                val createGame = createCreateGameTag()
                val pair = matchResult.groupValues[1] to matchResult.groupValues[2]
                insertFullEntity(pair)
                return InsertStackResult.Over(createGame, true)

            } else if (first is NestedTag.FullEntity) {
                //连续两个full entity
                val result = flushFullEntityWhenFirst()
                insertFullEntity(matchResult.groupValues[1] to matchResult.groupValues[2])
                return InsertStackResult.Over(result, true)
            }
            insertFullEntity(matchResult.groupValues[1] to matchResult.groupValues[2])
            return InsertStackResult.Success
        }

        //SHOW_ENTITY - Updating Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=85 zone=SETASIDE zonePos=0 cardId= player=2] CardID=SCH_231e
        matchResult = PowerParserImpl.SHOW_ENTITY.matchEntire(line)
        if (matchResult != null) {
            val first = nestedTagList.firstOrNull()
            val fullEntity = if (first is NestedTag.FullEntity) {
                flushFullEntityWhenFirst()
            } else null
            val entity = Entity.createFromContent(matchResult.groupValues[1])!!
            val cardId = matchResult.groupValues[2]
            nestedTagList.add(NestedTag.ShowEntity(entity, cardId))
            return if (fullEntity == null) {
                InsertStackResult.Success
            } else {
                InsertStackResult.Over(fullEntity, true)
            }


        }

        return InsertStackResult.CanNotInsert
    }


    /**
     * 如果栈中的第一个是FullEntity，就开始处理
     */
    private fun flushFullEntityWhenFirst(): PowerTag.PowerTaskList.FullEntity {


        val map = mutableMapOf<String, String>()
        val first = nestedTagList.removeFirst() as NestedTag.FullEntity



        nestedTagList.map {
            val result =
                it as? NestedTag.Tag ?: throw IllegalArgumentException("错误的类型，应该是Tag 但现在是 $it")
            result
        }.forEach {
            map[it.key] = it.value
        }

        //清空栈
        nestedTagList.clear()

        return PowerTag.PowerTaskList.FullEntity(
            first.entity,
            map,
            first.cardId
        )
//        return when {
//            isInsertCardToDeck(first) -> {
//                //插入一张卡牌到牌库
//                flushFullEntityInsertCardToDeck()
//            }
//            isInsertHeroToPlay(first) -> {
//                //放置英雄牌到战场
//                flushFullEntityInsertHeroToPlay()
//            }
//            isInsertHeroPowerToPlay(first) -> {
//                //放置英雄技能到战场
//                flushFullEntityInsertHeroPowerToPlay()
//            }
//            else -> throw RuntimeException("无法处理的 full entity $first")
//        }
    }

//    /**
//     * 是否是置入英雄技能到战场
//     */
//    private fun isInsertHeroPowerToPlay(fullEntity: NestedTag.FullEntity): Boolean {
//        if (fullEntity.entity.zone != Zone.Play) {
//            return false
//        }
//
//        nestedTagList.forEach {
//            if (it is NestedTag.Tag && "cardType".equals(
//                    it.key,
//                    true
//                ) && GameCardType.HeroPower.name.equals(
//                    it.value.replace("_", ""),
//                    true
//                )
//            ) {
//                return true
//            }
//        }
//
//
//        return false
//    }


//    private fun flushFullEntityInsertHeroPowerToPlay(): PowerTag.PowerTaskList.FullEntity.InsertHeroPowerToPlay {
//        var last = nestedTagList.removeLastOrNull()
//        //移除第一个
//        val entity = (nestedTagList.removeFirst() as NestedTag.FullEntity).entity
//        val map = mutableMapOf<String, String>()
//        while (last != null) {
//            when (last) {
//                is NestedTag.Tag -> {
//                    map[last.key] = last.value
//                }
//                else -> {
//                    throw RuntimeException("last的类型必须是Tag 但现在是 $last")
//                }
//            }
//            last = nestedTagList.removeLastOrNull()
//        }
//        return PowerTag.PowerTaskList.FullEntity.InsertHeroPowerToPlay.createFromEntityAndMap(
//            entity,
//            map
//        )
//    }
//
//    private fun flushFullEntityInsertHeroToPlay(): PowerTag.PowerTaskList.FullEntity.InsertHeroToPlay {
//        var last = nestedTagList.removeLastOrNull()
//        //移除第一个
//        val entity = (nestedTagList.removeFirst() as NestedTag.FullEntity).entity
//        val map = mutableMapOf<String, String>()
//        while (last != null) {
//            when (last) {
//                is NestedTag.Tag -> {
//                    map[last.key] = last.value
//                }
//                else -> {
//                    throw RuntimeException("last的类型必须是Tag 但现在是 $last")
//                }
//            }
//            last = nestedTagList.removeLastOrNull()
//        }
//        return PowerTag.PowerTaskList.FullEntity.InsertHeroToPlay.createFromEntityAndMap(
//            entity,
//            map
//        )
//    }

//    /**
//     * 是否是置入英雄卡到战场
//     */
//    private fun isInsertHeroToPlay(fullEntity: NestedTag.FullEntity): Boolean {
//        if (fullEntity.entity.zone != Zone.Play) {
//            return false
//        }
//
//        nestedTagList.forEach {
//            if (it is NestedTag.Tag && "cardType".equals(
//                    it.key,
//                    true
//                ) && GameCardType.Hero.name.equals(
//                    it.value,
//                    true
//                )
//            ) {
//                return true
//            }
//        }
//
//
//        return false
//    }

//    /**
//     * 是否是置入英雄卡到战场
//     */
//    private fun isInsertCardToDeck(fullEntity: NestedTag.FullEntity): Boolean {
//
//        return fullEntity.entity.zone == Zone.Deck && fullEntity.entity.gameCardType == GameCardType.Invalid
//    }

//    private fun flushFullEntityInsertCardToDeck(): PowerTag.PowerTaskList.FullEntity.InsertToDeck {
//        var last = nestedTagList.removeLastOrNull()
//        //移除第一个
//        val fullEntity = nestedTagList.removeFirst() as NestedTag.FullEntity
//        val map = mutableMapOf<String, String>()
//        while (last != null) {
//            when (last) {
//                is NestedTag.Tag -> {
//                    map[last.key] = last.value
//                }
//                else -> {
//                    throw RuntimeException("last的类型必须是Tag 但现在是 $last")
//                }
//            }
//            last = nestedTagList.removeLastOrNull()
//        }
//
//        if (map.size != 3) throw RuntimeException("在插入卡牌到牌库的情况下，tag数量必须是3个")
//
//        return PowerTag.PowerTaskList.FullEntity.InsertToDeck.createFromEntityAndMap(
//            fullEntity.entity,
//            map
//        )
//
//    }


    private fun insertFullEntity(pair: Pair<String, String>) {
        val fullEntity =
            createFullEntityByContent(pair.first, pair.second.ifEmpty { null })
        nestedTagList.add(fullEntity)
    }

    /**
     * 根据字符串创建FullEntity
     */
    //[entityName=UNKNOWN ENTITY [cardType=INVALID] id=4 zone=DECK zonePos=0 cardId= player=1]
    //[entityName=加尔鲁什·地狱咆哮 id=64 zone=PLAY zonePos=0 cardId=HERO_01 player=1]
    private fun createFullEntityByContent(content: String, cardId: String?): NestedTag.FullEntity {
        val entity: Entity = Entity.createFromContent(content)!!
        return NestedTag.FullEntity(entity, cardId)
    }


    private fun createCreateGameTag(): PowerTag.PowerTaskList.CreateGame {
        val first = nestedTagList.removeFirstOrNull()

        if (first == NestedTag.CreateGame) {
            val keyValueMap = mutableMapOf<String, String>()

            var last = nestedTagList.removeLastOrNull()
            var player1: Player? = null
            var player2: Player? = null
            while (last != null) {

                when (last) {

                    is NestedTag.GameEntity -> {
                        val game = GameEntity(
                            GameCardType.Game,
                            last.id
                        )
                        return PowerTag.PowerTaskList.CreateGame(
                            game,
                            player1!!,
                            player2!!
                        )
                    }
                    is NestedTag.Player -> {
                        keyValueMap["playerid"] = last.playerId.toString()
                        keyValueMap["entityid"] = last.entityId.toString()
                        if (player2 == null) {
                            player2 = Player.fromMap(keyValueMap)
                        } else {
                            player1 = Player.fromMap(keyValueMap)
                        }
                        keyValueMap.clear()
                    }
                    is NestedTag.Tag -> {
                        keyValueMap[last.key.replace("_", "").lowercase()] = last.value
                    }
                    else -> throw IllegalArgumentException("非法状态错误 $last")
                }

                last = nestedTagList.removeLastOrNull()
            }
        } else {
            throw IllegalArgumentException("第一个必须是 CreateGame，但现在是 $first")
        }

        throw RuntimeException("无法创建CreateGame")

    }


    private fun flushBlock(
        source: MutableList<NestedTag>,
    ): PowerTag.PowerTaskList.Block {
        //有可能出现多级嵌套
        val header = source.removeFirst()
        val first = header as? NestedTag.Block

        if (first == null) {


            throw RuntimeException("列表的第一个应该是Block，但现在是不是 现在是 $header,当前列表为 $source")
        }
        source.removeLast()

        val payloads = mutableListOf<PowerTag>()

        val tempList = mutableListOf<NestedTag>()

        source.forEachIndexed { index, nestedTag ->
            when (nestedTag) {
                is NestedTag.Block -> {
//                    if (tempList.isEmpty()) {
                    tempList.add(nestedTag)
//                    } else {
//                        val blockStartCount = tempList.count {
//                            it is NestedTag.Block
//                        }
//                        if (blockStartCount != 1) {
//                            throw IllegalArgumentException("错误的block数量 $blockStartCount")
//                        }
//
//                        val pairedBlockEndIndex = findPairBlockEndIndex(source, index)
//                        if (pairedBlockEndIndex == -1) {
//                            throw IllegalArgumentException("找不到配对的结束标识")
//                        }
//                        val innerBlockList = source.subList(index, pairedBlockEndIndex)
//                        tempList.add(flushBlock(innerBlockList))
//                    }
                }

                is NestedTag.FullEntity -> {
                    if (tempList.isNotEmpty()) {
                        tempList.add(nestedTag)
                    } else {
                        payloads.add(
                            PowerTag.PowerTaskList.FullEntity(
                                nestedTag.entity,
                                mutableMapOf(),
                                nestedTag.cardId
                            )
                        )
                    }
                }

                is NestedTag.ShowEntity -> {
                    if (tempList.isNotEmpty()) {
                        tempList.add(nestedTag)
                    } else {
                        payloads.add(
                            PowerTag.PowerTaskList.ShowEntity(
                                nestedTag.entity, nestedTag.cardId
                            )
                        )
                    }
                }
                is NestedTag.Tag -> {


                    if (tempList.isNotEmpty()) {
                        tempList.add(nestedTag)
                    } else {
                        val last = payloads.last()
                        if (last is PowerTag.PowerTaskList.FullEntity) {
                            last.append(nestedTag.toPair())
                        } else if (last is PowerTag.PowerTaskList.ShowEntity) {
                            last.append(nestedTag.toPair())
                        }
                    }
                }


                is NestedTag.TagChange -> {
                    if (tempList.isNotEmpty()) {
                        tempList.add(nestedTag)
                    } else {
                        payloads.add(nestedTag.convert())
                    }
                }
                NestedTag.BlockEnd -> {
                    tempList.add(NestedTag.BlockEnd)

                    val blockStartCount = tempList.count {
                        it is NestedTag.Block
                    }
                    val blockEndCount = tempList.count {
                        it is NestedTag.BlockEnd
                    }
                    if (blockStartCount == blockEndCount) {
                        payloads.add(flushBlock(tempList))
                        tempList.clear()
                    }
                }
                else -> {
                    throw IllegalArgumentException("非法的数据 $nestedTag")
                }
            }

        }
        source.clear()

        return PowerTag.PowerTaskList.Block(
            first.blockType,
            first.entity,
            first.target,
            payloads
        )
    }
}

private fun findPairBlockEndIndex(source: List<NestedTag>, start: Int): Int {
    var blockStartCount = 0
    source.subList(start, source.size).forEachIndexed { index, it ->
        if (it is NestedTag.Block) {
            blockStartCount++
        } else if (it is NestedTag.BlockEnd) {
            if (blockStartCount == 0) {
                return index
            }
            blockStartCount--

        }
    }

    return -1
}