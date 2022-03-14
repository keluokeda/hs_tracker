package com.ke.hs_tracker.module.domain

import android.util.Base64
import com.ke.hs_tracker.module.db.CardDao
import com.ke.hs_tracker.module.di.IoDispatcher
import com.ke.hs_tracker.module.entity.Card
import com.ke.hs_tracker.module.entity.CardBean
import com.ke.mvvm.base.domian.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ParseDeckCodeUseCase
@Inject constructor(
    private val cardDao: CardDao,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<String, List<CardBean>>(dispatcher) {
    override suspend fun execute(parameters: String): List<CardBean> {
        val allCards = cardDao.getAll()

        val byteArray =
            Base64.decode(parameters, Base64.DEFAULT)
//                deckString.encodeToByteArray().decodeBase64()
        //[0, 1, 2, 1, -3, 4, 8, -116, -71, 3, -32, -52, 3, -65, -32, 3, -109, -31, 3, -78, -9, 3, -96, -118, 4, -104, -115, 4, -4, -98, 4, 11, -63, -72, 3, -127, -65, 3, -51, -50, 3, -5, -35, 3, -21, -34, 3, -48, -20, 3, -47, -20, 3, -89, -9, 3, -118, -115, 4, -3, -98, 4, -5, -94, 4, 0]
//        val size = byteArray.size
        val byteList = mutableListOf<Byte>()
        byteArray.forEach {
            byteList.add(it)
        }

        val keep = byteList.removeFirst()//移除第一个保留的字段
//            assert(byteList.removeFirst().toInt() == 0)
//            assert(byteList.removeFirst().toInt() != 0)//总是1
        val version = byteList.removeFirst()
        val cardType = byteList.removeFirst()//1是标准 2是狂野
        val cardList = mutableListOf<CardBean>()
        val heroCount = getVarInt(byteList)
        for (i in 0 until heroCount) {
            val id = getVarInt(byteList)
            val hero =
                findByDbfId(id, allCards) ?: throw IllegalArgumentException("找不到id为 $id  的卡牌")
            hero.count = heroCount
            cardList.add(hero)
        }

        for (i in 1..3) {
            val c = getVarInt(byteList)
            for (j in 0 until c) {
                val dbfId = getVarInt(byteList)
                val count: Int
                if (i == 3) {
                    count = getVarInt(byteList)
                } else {
                    count = i
                }
//                    result.cards.add(Card(dbfId, count))
                val jsonObject =
                    findByDbfId(dbfId, allCards)
                        ?: throw IllegalArgumentException("找不到id为 $dbfId  的卡牌")
                jsonObject.count = count
                cardList.add(jsonObject)
            }
        }

        //移除英雄
        cardList.removeFirst()

        cardList.sortBy {
            it.card.cost
        }

        return cardList
    }

    /**
     * 获得无符号int
     */
    private fun getVarInt(src: MutableList<Byte>): Int {
        var result = 0
        var shift = 0
        var b: Int

        do {
            if (shift >= 32) {
                // Out of range
                throw IndexOutOfBoundsException("varint too long")
            }
            // Get 7 bits from next byte
            b = src.removeFirst().toInt()
            result = result or (b and 0x7F shl shift)
            shift += 7
        } while (b and 0x80 != 0)
        return result
    }

    private fun findByDbfId(id: Int, cardEntityList: List<Card>): CardBean? {
        val cardEntity = cardEntityList.find { it.dbfId == id } ?: return null

        return CardBean(cardEntity)
    }
}