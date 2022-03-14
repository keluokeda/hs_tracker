package com.ke.hs_tracker.module.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleDialogCardPreviewBinding
import com.ke.hs_tracker.module.databinding.ModuleItemCardBinding
import com.ke.hs_tracker.module.entity.CardBean
import com.ke.mvvm.base.ui.BaseViewBindingAdapter

class CardAdapter : BaseViewBindingAdapter<CardBean, ModuleItemCardBinding>() {


    init {
        setOnItemClickListener { _, _, position ->
            val item = getItem(position)

            val binding = ModuleDialogCardPreviewBinding.inflate(LayoutInflater.from(context))
            AlertDialog.Builder(context)
                .show().apply {
                    window?.run {
                        setContentView(binding.root)
                        binding.root.setOnClickListener {
                            dismiss()
                        }
                        //去掉对话框的白色背景
                        setBackgroundDrawableResource(android.R.color.transparent)
                    }
                }
            Glide.with(binding.image)
                .load("https://art.hearthstonejson.com/v1/render/latest/zhCN/512x/${item.card.id}.png")
                .placeholder(R.mipmap.ic_launcher)
                .into(binding.image)
        }
    }

    override fun bindItem(
        item: CardBean,
        viewBinding: ModuleItemCardBinding,
        viewType: Int,
        position: Int
    ) {
        viewBinding.name.text = item.card.name + " " + item.count
        viewBinding.cost.text = item.card.cost.toString()
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ModuleItemCardBinding {
        return ModuleItemCardBinding.inflate(inflater, parent, false)
    }
}