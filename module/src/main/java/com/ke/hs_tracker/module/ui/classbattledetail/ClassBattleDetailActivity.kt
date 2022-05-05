package com.ke.hs_tracker.module.ui.classbattledetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.ke.hs_tracker.module.databinding.ModuleActivityClassBattleDetailBinding
import com.ke.hs_tracker.module.databinding.ModuleItemClassBattleDetailBinding
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.mvvm.base.data.ViewStatus
import com.ke.mvvm.base.ui.BaseViewBindingAdapter
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ClassBattleDetailActivity : AppCompatActivity() {

    internal val viewModel: ClassBattleDetailViewModel by viewModels()

    private val adapter by lazy {
        object : BaseViewBindingAdapter<ClassBattleItem, ModuleItemClassBattleDetailBinding>() {
            override fun bindItem(
                item: ClassBattleItem,
                viewBinding: ModuleItemClassBattleDetailBinding,
                viewType: Int,
                position: Int
            ) {

                viewBinding.apply {
                    value1.setText(item.hero.titleRes)
                    value2.text = item.times.toString()
                    value3.text = item.win.toString()
                    value4.text = item.loss
                        .toString()
                    value5.text = "${item.rate}%"
                }
            }

            override fun createViewBinding(
                inflater: LayoutInflater,
                parent: ViewGroup,
                viewType: Int
            ): ModuleItemClassBattleDetailBinding {
                return ModuleItemClassBattleDetailBinding.inflate(inflater, parent, false)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.module_activity_class_battle_detail)
        val binding = ModuleActivityClassBattleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        launchAndRepeatWithViewLifecycle {
            viewModel.viewStatus.collect {
                when (it) {
                    is ViewStatus.Loading -> {

                    }
                    is ViewStatus.Content -> {
                        adapter.setList(it.data)
                    }
                    is ViewStatus.Error -> {

                    }
                }
            }
        }
    }

    companion object {
        fun createIntent(context: Context, cardClass: CardClass): Intent {
            return Intent(context, ClassBattleDetailActivity::class.java).apply {
                putExtra(EXTRA_CARD_CLASS, cardClass)
            }
        }

        internal const val EXTRA_CARD_CLASS = "extra_card_class"
    }
}