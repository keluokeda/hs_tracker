package com.ke.hs_tracker.module.ui.summary

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleItemSummaryBattleBinding
import com.ke.hs_tracker.module.ui.classbattledetail.ClassBattleDetailActivity
import com.ke.hs_tracker.module.ui.deckbattledetail.DeckBattleDetailActivity
import com.ke.mvvm.base.ui.BaseViewBindingAdapter

internal class BattleRateItemAdapter :
    BaseViewBindingAdapter<BattleRateItem, ModuleItemSummaryBattleBinding>() {
    override fun bindItem(
        item: BattleRateItem,
        viewBinding: ModuleItemSummaryBattleBinding,
        viewType: Int,
        position: Int
    ) {
        viewBinding.apply {
            allCount.text = "总：" + (item.lostCount + item.winCount).toString()
            winCount.text = "胜：" + item.winCount.toString()
            lostCount.text = "负：" + item.lostCount.toString()

            winRate.text = item.rate.toString() + "%"
            when (item) {
                is BattleRateItem.ClassBattleRate -> {
                    image.setImageResource(item.cardClass.roundIcon!!)
                    name.setText(item.cardClass.titleRes)
                    root.setOnClickListener {
                        it.context.startActivity(
                            ClassBattleDetailActivity.createIntent(
                                it.context,
                                item.cardClass
                            )
                        )
                    }
                }
                is BattleRateItem.DeckBattleRate -> {
                    image.setImageResource(R.drawable.module_image_round_demon_hunter)
                    name.text = item.deckName
                    root.setOnClickListener {
                        it.context.startActivity(
                            DeckBattleDetailActivity.createIntent(
                                it.context,
                                item.deckCode,
                                item.deckName
                            )
                        )
                    }
                }
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ModuleItemSummaryBattleBinding {
        return ModuleItemSummaryBattleBinding.inflate(inflater, parent, false)
    }


}