package com.ke.hs_tracker.module.ui.zoneevents

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.db.CardDao
import com.ke.hs_tracker.module.db.ZonePositionChangedEvent
import com.ke.hs_tracker.module.db.ZonePositionChangedEventDao
import com.ke.hs_tracker.module.entity.Card
import com.ke.hs_tracker.module.entity.Zone
import com.ke.hs_tracker.module.entity.ZoneCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class ZoneEventsViewModel @Inject constructor(
    private val zonePositionChangedEventDao: ZonePositionChangedEventDao,
    private val cardDao: CardDao,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _currentFragmentIndex = MutableStateFlow(0)

    internal val currentFragmentIndex: StateFlow<Int>
        get() = _currentFragmentIndex

    private val gameId = savedStateHandle.get<String>(ZoneEventsActivity.EXTRA_KEY_ID)!!

    private val gameCardCollectionsList = mutableListOf<GameCardCollections>().apply {
        add(GameCardCollections())
    }

    private val _collectionsList = MutableStateFlow<List<GameCardCollections>>(emptyList())

    internal val collectionsList: StateFlow<List<GameCardCollections>>
        get() = _collectionsList

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val allCard = cardDao.getAll()
                val list = zonePositionChangedEventDao.getAllByGameId(gameId)

                if (list.isEmpty()) {
                    return@withContext
                }
                val stack = mutableListOf<ZonePositionChangedEvent>()

                list.forEach {

                    if (it.entityId == stack.lastOrNull()?.entityId) {
                        stack.add(it)
                    } else {
                        flushStack(stack, gameCardCollectionsList, allCard)
                    }

                    if (stack.isEmpty()) {
                        stack.add(it)
                    }
                }

                flushStack(stack, gameCardCollectionsList, allCard)

                _currentFragmentIndex.value = 1

                _collectionsList.value = gameCardCollectionsList

            }

        }
    }


    companion object {
        private fun flushStack(
            list: MutableList<ZonePositionChangedEvent>,
            mutableList: MutableList<GameCardCollections>,
            allCardList: List<Card>
        ) {

            val last = mutableList.last()
            if (list.isEmpty()) {
                return
            }
            when (list.size) {
                1 -> {
                    //仅改变区域
                    val event = list.first()

                    mutableList.add(last.update(event, allCardList))

                }
                2 -> {
                    //改变区域和位置
                    val first = list[0]
                    val second = list[1]


                    mutableList.add(last.update(first.plus(second), allCardList))
                }
                3 -> {
                    val first = list[0]
                    val second = list[1]
                    val third = list[2]
                    mutableList.add(last.update(first.plusPlus(second, third), allCardList))

                }
                else -> {
                    list.forEach {
                        flushStack(
                            mutableListOf(it), mutableList, allCardList
                        )
                    }
                }
            }
            list.clear()
        }
    }
}

@Parcelize
internal data class GameCardCollections(
    private val _userDeckCardList: MutableList<ZoneCard> = mutableListOf(),
    private val _opponentDeckCardList: MutableList<ZoneCard> = mutableListOf(),
    private val _userHandCardList: MutableList<ZoneCard> = mutableListOf(),
    private val _opponentHandCardList: MutableList<ZoneCard> = mutableListOf(),
    private val _userPlayCardList: MutableList<ZoneCard> = mutableListOf(),
    private val _opponentPlayCardList: MutableList<ZoneCard> = mutableListOf(),
    private val _userGraveyardCardList: MutableList<ZoneCard> = mutableListOf(),
    private val _opponentGraveyardCardList: MutableList<ZoneCard> = mutableListOf(),
    private val _userSecretCardList: MutableList<ZoneCard> = mutableListOf(),
    private val _opponentSecretCardList: MutableList<ZoneCard> = mutableListOf(),

    ) : Parcelable {

    /**
     * 深拷贝
     */
    private fun deepClone(): GameCardCollections {
        var parcel: Parcel? = null
        try {
            parcel = Parcel.obtain()
            parcel.writeParcelable(this, 0)

            parcel.setDataPosition(0)
            return parcel.readParcelable(this.javaClass.classLoader)!!
        } finally {

            parcel?.recycle()
        }
    }

    val userDeckCardList: List<ZoneCard>
        get() = _userDeckCardList

    val opponentDeckCardList: List<ZoneCard>
        get() = _opponentDeckCardList

    val userHandCardList: List<ZoneCard>
        get() = _userHandCardList

    val opponentHandCardList: List<ZoneCard>
        get() = _opponentHandCardList

    val userPlayCardList: List<ZoneCard>
        get() = _userPlayCardList

    val opponentPlayCardList: List<ZoneCard>
        get() = _opponentPlayCardList

    val userGraveyardCardList: List<ZoneCard>
        get() = _userGraveyardCardList

    val opponentGraveyardCardList: List<ZoneCard>
        get() = _opponentGraveyardCardList

    val userSecretCardList: List<ZoneCard>
        get() = _userSecretCardList

    val opponentSecretCardList: List<ZoneCard>
        get() = _opponentSecretCardList

    fun update(event: ZonePositionChangedEvent, allCardList: List<Card>): GameCardCollections {

        val copy = deepClone()

        val oldList = when (event.currentZone) {
            Zone.Play -> if (event.isUser) copy._userPlayCardList else copy._opponentPlayCardList
            Zone.Deck -> if (event.isUser) copy._userDeckCardList else copy._opponentDeckCardList

            Zone.Graveyard -> if (event.isUser) copy._userGraveyardCardList else copy._opponentGraveyardCardList
            Zone.Hand -> if (event.isUser) copy._userHandCardList else copy._opponentHandCardList

            Zone.Secret -> if (event.isUser) copy._userSecretCardList else copy._opponentSecretCardList
            Zone.SetAside, Zone.RemovedFromGame -> null

            else -> throw RuntimeException("oldList 不支持的区域类型 ${event.currentZone}")
        }

        val newList = when (event.newZone) {
            Zone.Play -> if (event.isUser) copy._userPlayCardList else copy._opponentPlayCardList
            Zone.Deck -> if (event.isUser) copy._userDeckCardList else copy._opponentDeckCardList

            Zone.Graveyard -> if (event.isUser) copy._userGraveyardCardList else copy._opponentGraveyardCardList
            Zone.Hand -> if (event.isUser) copy._userHandCardList else copy._opponentHandCardList

            Zone.Secret -> if (event.isUser) copy._userSecretCardList else copy._opponentSecretCardList
            //移除掉
            Zone.SetAside, Zone.RemovedFromGame -> null

            else -> {

                throw RuntimeException("newList 不支持的区域类型 ${event.newZone}")
            }
        }

        if (event.currentZone == event.newZone) {
            addCardToList(newList, allCardList, event)
        } else {
            oldList?.removeAll {
                it.entityId == event.entityId
            }

            addCardToList(newList, allCardList, event)
        }




        return copy

    }

    private fun addCardToList(
        newList: MutableList<ZoneCard>?,
        allCardList: List<Card>,
        event: ZonePositionChangedEvent
    ) {
        val target = newList ?: emptyList()

        if (target.find {
                it.entityId == event.entityId
            } != null) {
            //存在的话就不能插入
            return
        }

        newList?.add(
            ZoneCard(
                allCardList.find { it.id == event.cardId },
                event.entityId,
                event.newPosition
            )
        )
    }
}

