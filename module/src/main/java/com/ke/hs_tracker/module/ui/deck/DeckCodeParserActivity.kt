package com.ke.hs_tracker.module.ui.deck

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.ke.hs_tracker.module.databinding.ModuleActivityDeckCodeParserBinding
import com.ke.hs_tracker.module.ui.common.CardAdapter
import com.ke.mvvm.base.data.ViewStatus
import com.ke.mvvm.base.ui.collectLoadingDialog
import com.ke.mvvm.base.ui.collectSnackbarFlow
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DeckCodeParserActivity : AppCompatActivity() {
    private val viewModel: DeckCodeParserViewModel by viewModels()
    private lateinit var binding: ModuleActivityDeckCodeParserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityDeckCodeParserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        collectLoadingDialog(viewModel)
        collectSnackbarFlow(viewModel)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        val adapter = CardAdapter()
        binding.recyclerView.adapter = adapter
        binding.start.setOnClickListener {
            viewModel.start(
                binding.code.text?.toString() ?: ""
            )
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.viewStatus.collect {
                when (it) {
                    is ViewStatus.Loading -> {

                    }
                    is ViewStatus.Content -> {

                        adapter.setList(it.data.apply {
                        })

                    }
                    is ViewStatus.Error -> {

                    }
                }
            }
        }
    }


}