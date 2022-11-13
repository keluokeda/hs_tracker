package com.ke.hs_tracker.module.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.ke.hs_tracker.module.bindCard
import com.ke.hs_tracker.module.databinding.ModuleItemCardBinding
import com.ke.hs_tracker.module.entity.CardBean
import com.ke.mvvm.base.ui.BaseViewBindingAdapter

class CardAdapter : BaseViewBindingAdapter<CardBean, ModuleItemCardBinding>() {



    init {

        setDiffCallback(object : DiffUtil.ItemCallback<CardBean>() {
            override fun areItemsTheSame(oldItem: CardBean, newItem: CardBean): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CardBean, newItem: CardBean): Boolean {
                return oldItem == newItem
            }

        })
    }

    override fun bindItem(
        item: CardBean,
        viewBinding: ModuleItemCardBinding,
        viewType: Int,
        position: Int
    ) {

        viewBinding.bindCard(item.card)
//        viewBinding.name.text = item.card.name
//        viewBinding.cost.text = item.card.cost.toString()
        viewBinding.count.text = item.count.toString()

//        item.card.rarity?.apply {
//            viewBinding.name.setTextColor(
//                ResourcesCompat.getColor(
//                    viewBinding.root.context.resources,
//                    colorRes,
//                    null
//                )
//            )
//
//        }
//
//        Glide.with(viewBinding.imageTile)
//            .load("https://art.hearthstonejson.com/v1/tiles/${item.card.id}.png")
//            .into(viewBinding.imageTile)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ModuleItemCardBinding {
        return ModuleItemCardBinding.inflate(inflater, parent, false)
    }
}