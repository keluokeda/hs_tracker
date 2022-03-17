package com.ke.hs_tracker.module.ui.filter

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivityFilterBinding
import com.ke.hs_tracker.module.databinding.ModuleItemChipFilterBinding
import com.ke.hs_tracker.module.entity.Card
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.hs_tracker.module.entity.CardType

class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ModuleActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cardList = intent.getParcelableArrayListExtra<Card>(EXTRA_CARD_LIST)?.toList()
            ?: throw RuntimeException("cardList 不能为空")

        if (cardList.isEmpty()) {
            finish()
        }

        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            binding.chipGroupClass.isVisible = id == R.id.rb_class
            binding.chipGroupRarity.isVisible = id == R.id.rb_rarity
            binding.chipGroupRace.isVisible = id == R.id.rb_minion
            binding.chipGroupSpell.isVisible = id == R.id.rb_spell
            binding.chipGroupWeapon.isVisible = id == R.id.rb_weapon
            binding.chipGroupHero.isVisible = id == R.id.rb_hero
            binding.chipGroupMechanics.isVisible = id == R.id.rb_mechanics
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

//        val cardClasses: List<Pair<CardClass, Int>> =
        cardList.map {
            if (it.classes.isEmpty() && it.cardClass != null) {
                listOf(it.cardClass)
            } else if (it.classes.isNotEmpty()) {
                it.classes
            } else {
                emptyList()
            }
        }.filter {
            it.isNotEmpty()
        }.groupBy {
            it
        }.map {
            it.key to it.value.size
        }.forEach {


            val b = ModuleItemChipFilterBinding.inflate(layoutInflater)
            b.root.setText(
                "${getName(it.first, applicationContext)} ${it.second}"
            )
            b.root.tag = it.first
            binding.chipGroupClass
                .addView(b.root)
        }

        if (binding.chipGroupClass.childCount == 0) {
            binding.rbClass.isEnabled = false
            binding.rbClass.isChecked = false
        } else {
            binding.rbClass.isChecked = true
        }

        cardList.mapNotNull {
            it.rarity
        }.sortedBy {
            it.ordinal
        }
            .groupBy {
                it
            }.map {
                it.key to it.value.size
            }.forEach {
                val b = ModuleItemChipFilterBinding.inflate(layoutInflater)
                b.root.setText(
                    "${getString(it.first.titleRes)} ${it.second}"
                )
                binding.chipGroupRarity
                    .addView(b.root)
            }



        cardList.mapNotNull {
            it.race
        }.groupBy {
            it
        }.map {
            it.key to it.value.size
        }.forEach {
            val b = ModuleItemChipFilterBinding.inflate(layoutInflater)
            b.root.setText(
                "${getString(it.first.titleRes!!)} ${it.second}"
            )
            binding.chipGroupRace
                .addView(b.root)
        }


        if (binding.chipGroupRace.childCount == 0) {
            //没有随从
            binding.rbMinion.isChecked = false
            binding.rbMinion.isEnabled = false
        }

        cardList.mapNotNull {
            it.spellSchool
        }.groupBy {
            it
        }.map {
            it.key to it.value.size
        }.forEach {
            val b = ModuleItemChipFilterBinding.inflate(layoutInflater)
            b.root.setText(
                "${getString(it.first.titleRes!!)} ${it.second}"
            )
            binding.chipGroupSpell
                .addView(b.root)
        }

        if (binding.chipGroupSpell.childCount == 0) {
            binding.rbSpell.isChecked = false
            binding.rbSpell.isEnabled = false
        }

        val weaponCount = cardList.count {
            it.type == CardType.Weapon
        }
        if (weaponCount == 0) {
            binding.rbWeapon.isChecked = false
            binding.rbWeapon.isEnabled = false
        } else {
            val b = ModuleItemChipFilterBinding.inflate(layoutInflater)
            b.root.setText(
                "${getString(R.string.module_weapon)} ${weaponCount}"
            )
            binding.chipGroupWeapon
                .addView(b.root)
        }

        val heroCount = cardList.count {
            it.type == CardType.Hero
        }
        if (heroCount == 0) {
            binding.rbHero.isChecked = false
            binding.rbHero.isEnabled = false
        } else {
            val b = ModuleItemChipFilterBinding.inflate(layoutInflater)
            b.root.setText(
                "${getString(R.string.module_hero)} ${heroCount}"
            )
            binding.chipGroupHero
                .addView(b.root)
        }

        cardList.flatMap {
            it.mechanics
        }.groupBy { it }
            .map {
                it.key to it.value.size
            }.forEach {

                val b = ModuleItemChipFilterBinding.inflate(layoutInflater)
                b.root.setText(
                    "${it.first.name} ${it.second}"
                )
                binding.chipGroupMechanics
                    .addView(b.root)
            }
        if (binding.chipGroupMechanics.childCount == 0) {
            binding.rbMechanics.isChecked = false
            binding.rbMechanics.isEnabled = false
        }
//            cardList.flatMap {
//                it.classes
//            }.filter { it.display }
//                .groupBy {
//                    it
//                }.map {
//                    it.key to it.value.size
//                }

//            cardList.groupBy {
//            it.cardClass!!
//        }.map {
//            it.key to it.value.count()
//        }

        //        CardClass.values()
//            .filter {
//                it.display
//            }
//            .forEach {
//                val chip =
//                    ModuleItemChipFilterBinding.inflate(layoutInflater).root
//
//                val isBlackTextColor = it.blackText
//
//                if (isBlackTextColor) {
//                    chip.setTextColor(Color.BLACK)
//                    chip.setCheckedIconResource(R.drawable.module_baseline_done_black_24dp)
//                } else {
//                    chip.setTextColor(Color.WHITE)
//                    chip.setCheckedIconResource(R.drawable.module_baseline_done_white_24dp)
//                }
//                chip.setChipBackgroundColorResource(it.color)
//                chip.setText(it.titleRes)
//                binding.chipGroupClass.addView(chip)
//            }
//
//        Rarity.values().forEach {
//            val chip =
//                ModuleItemChipFilterBinding.inflate(layoutInflater).root
//            chip.setText(it.title)
//            binding.chipGroupRarity.addView(chip)
//        }

    }


    companion object {
        const val EXTRA_CARD_LIST = "EXTRA_CARD_LIST"

        private fun getName(list: List<CardClass>, context: Context): String {
            if (list.isEmpty()) {
                throw RuntimeException("list 不能为空")
            }
            if (list.size == 1) {
                return context.getString(list.first().titleRes)
            }

            val stringBuilder = StringBuilder()
            stringBuilder.append("(")

            list.map {
                context.getString(it.titleRes)
            }.forEachIndexed { index, s ->
                stringBuilder.append(s)
                if (index != list.size - 1) {
                    stringBuilder.append(" ")
                }
            }
            stringBuilder.append(")")

            return stringBuilder.toString()
        }
    }
}