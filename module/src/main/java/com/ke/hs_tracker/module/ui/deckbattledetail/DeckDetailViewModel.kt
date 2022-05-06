package com.ke.hs_tracker.module.ui.deckbattledetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ke.hs_tracker.module.domain.ParseDeckCodeUseCase
import com.ke.hs_tracker.module.entity.CardType
import com.ke.hs_tracker.module.entity.Mechanics
import com.ke.hs_tracker.module.entity.Race
import com.ke.hs_tracker.module.entity.SpellSchool
import com.ke.mvvm.base.data.successOr
import com.ke.mvvm.base.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckDetailViewModel
@Inject constructor(
    private val parseDeckCodeUseCase: ParseDeckCodeUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _costList = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())

    /**
     * 法力曲线
     */
    internal val costList: StateFlow<List<Pair<Int, Int>>>
        get() = _costList

    private val _attackList = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())

    /**
     * 攻击力曲线
     */
    internal val attackList: StateFlow<List<Pair<Int, Int>>>
        get() = _attackList

    private val _mechanics = MutableStateFlow<List<Pair<Mechanics, Int>>>(emptyList())

    /**
     * 卡牌效果
     */
    internal val mechanics: StateFlow<List<Pair<Mechanics, Int>>>
        get() = _mechanics

    private val _raceList = MutableStateFlow<List<Pair<Race, Int>>>(emptyList())

    //随从种族
    internal val raceList: StateFlow<List<Pair<Race, Int>>>
        get() = _raceList

    private val _spellSchoolList = MutableStateFlow<List<Pair<SpellSchool, Int>>>(emptyList())
    internal val spellSchoolList: StateFlow<List<Pair<SpellSchool, Int>>>
        get() = _spellSchoolList

    init {
        viewModelScope.launch {
            val code = savedStateHandle.get<String>(DeckBattleDetailActivity.EXTRA_DECK_CODE)!!

            val cardList = parseDeckCodeUseCase(code).successOr(emptyList())
            _costList.value = cardList
                .flatMap {
                    it.toCardList()
                }
                .groupBy {
                    val cost =
                        it.cost
                    if (cost > MAX_VALUE) MAX_VALUE else cost
                }
                .map {
                    it.key to it.value.size
                }

            _attackList.value = cardList
                .flatMap {
                    it.toCardList()
                }
                .filter {
                    //必须是随从牌或武器牌
                    it.type == CardType.Minion || it.type == CardType.Weapon
                }.groupBy {
                    if (
                        it.attack >= MAX_VALUE
                    ) MAX_VALUE else it.attack
                }.map {
                    it.key to it.value.size
                }



            _mechanics.value = cardList
                .flatMap {
                    it.toCardList()
                }
                .flatMap {
                    it.mechanics
                }.groupBy {
                    it
                }.map {
                    it.key to it.value.size
                }

            val raceMap = mutableMapOf<Race, Int>()

            var allRace = 0

            cardList.flatMap {
                it.toCardList()
            }.mapNotNull {
                it.race
            }.forEach {
                if (it.tradition) {
                    if (it != Race.All) {
                        var count = raceMap[it] ?: 0
                        count += 1
                        raceMap[it] = count
                    } else if (it == Race.All) {
                        allRace += 1
                    }
                }
            }

            _raceList.value = raceMap.map {
                it.key to it.value + allRace
            }

            _spellSchoolList.value = cardList.flatMap {
                it.toCardList()
            }.mapNotNull {
                it.spellSchool
            }.groupBy {
                it
            }.map {
                it.key to it.value.size
            }

        }
    }
}

private const val MAX_VALUE = 7