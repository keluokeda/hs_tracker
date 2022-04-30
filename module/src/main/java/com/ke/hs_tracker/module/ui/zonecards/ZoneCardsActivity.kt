package com.ke.hs_tracker.module.ui.zonecards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.ke.hs_tracker.module.databinding.ModuleActivityZoneCardsBinding
import com.ke.hs_tracker.module.databinding.ModuleItemZoneCardBinding
import com.ke.hs_tracker.module.entity.ZoneCard
import com.ke.mvvm.base.ui.BaseViewBindingAdapter

class ZoneCardsActivity : AppCompatActivity() {

    private val adapter = object : BaseViewBindingAdapter<ZoneCard, ModuleItemZoneCardBinding>() {
        override fun bindItem(
            item: ZoneCard,
            viewBinding: ModuleItemZoneCardBinding,
            viewType: Int,
            position: Int
        ) {
            viewBinding.apply {
                cost.text = item.card?.cost?.toString() ?: ""
                name.text = item.card?.name ?: "未知卡牌"
                this.position.text = item.position.toString()
                item.card?.id?.let {
                    Glide.with(this@ZoneCardsActivity)
                        .load("https://art.hearthstonejson.com/v1/tiles/${it}.png")
                        .into(imageTile)
                }
            }
        }

        override fun createViewBinding(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ): ModuleItemZoneCardBinding {
            return ModuleItemZoneCardBinding.inflate(inflater, parent, false)
        }

    }

    private lateinit var binding: ModuleActivityZoneCardsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityZoneCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        val zoneCardList = intent.getParcelableArrayListExtra<ZoneCard>(EXTRA_KEY_ZONE_CARD_LIST)

        adapter.setList(zoneCardList?.sortedBy {
            it.position
        })


    }

    companion object {
        const val EXTRA_KEY_ZONE_CARD_LIST = "EXTRA_KEY_ZONE_CARD_LIST"
    }
}