package com.ke.hs_tracker.module.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.bindCard
import com.ke.hs_tracker.module.databinding.ModuleDialogCardPreviewBinding
import com.ke.hs_tracker.module.databinding.ModuleItemCardBinding
import com.ke.hs_tracker.module.entity.CardBean
import com.ke.hs_tracker.module.showCardImageDialog
import com.ke.mvvm.base.ui.BaseViewBindingAdapter

class CardAdapter : BaseViewBindingAdapter<CardBean, ModuleItemCardBinding>() {


    init {
        setOnItemClickListener { _, _, position ->
            val item = getItem(position)

            showCardImageDialog(context,item.card.id)
//            val binding = ModuleDialogCardPreviewBinding.inflate(LayoutInflater.from(context))
//            AlertDialog.Builder(context)
//                .show().apply {
//                    window?.run {
//                        setContentView(binding.root)
//                        binding.root.setOnClickListener {
//                            dismiss()
//                        }
//                        //去掉对话框的白色背景
//                        setBackgroundDrawableResource(android.R.color.transparent)
//                    }
//                }
//            Glide.with(binding.image)
//                .load("https://art.hearthstonejson.com/v1/render/latest/zhCN/512x/${item.card.id}.png")
//                .placeholder(R.mipmap.ic_launcher)
//                .into(binding.image)
        }
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