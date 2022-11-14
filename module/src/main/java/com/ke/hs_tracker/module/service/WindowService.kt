package com.ke.hs_tracker.module.service

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleFloatingWindowBinding
import com.ke.hs_tracker.module.log
import com.ke.hs_tracker.module.parser.DeckCardObserver
import com.ke.hs_tracker.module.ui.common.CardAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WindowService : LifecycleService() {


    private val windowManager: WindowManager by lazy {
        getSystemService(WINDOW_SERVICE) as WindowManager
    }

    private val binding: ModuleFloatingWindowBinding by lazy {
        val layoutInflater = LayoutInflater.from(applicationContext)
        ModuleFloatingWindowBinding.inflate(layoutInflater)
    }

    private val deckAdapter = CardAdapter()

    private val graveyardAdapter = CardAdapter()

    private val opponentGraveyardAdapter = CardAdapter()

    @Inject
    lateinit var deckCardObserver: DeckCardObserver


    private fun showView() {
        val layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams.width = resources.getDimension(R.dimen.module_floating_window_width).toInt()
        layoutParams.height = resources.getDimension(R.dimen.module_floating_window_height).toInt()
        //需要设置 这个 不然空白地方无法点击
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//        layoutParams.alpha = 0.8f

        layoutParams.gravity = Gravity.START or Gravity.TOP

        windowManager.addView(binding.root, layoutParams)

        binding.recyclerView.adapter = deckAdapter

        binding.scale.setOnTouchListener(
            ScaleTouchListener(windowManager, binding.root, layoutParams)
        )

//        binding.zoom.setOnClickListener {
//            binding.recyclerView.isVisible = !binding.recyclerView.isVisible
//        }

        binding.close.setOnClickListener {
            windowManager.removeView(binding.root)
            stopSelf()
        }

        binding.spinner.adapter = ArrayAdapter.createFromResource(
            applicationContext,
            R.array.module_spinner,
            android.R.layout.simple_list_item_1
        )
        binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        binding.recyclerView.adapter = deckAdapter
                    }
                    1 -> {
                        binding.recyclerView.adapter = graveyardAdapter
                    }
                    2 -> {
                        binding.recyclerView.adapter = opponentGraveyardAdapter
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        binding.spinner.setSelection(0)

        binding.root.setOnTouchListener(
            ItemViewTouchListener(
                layoutParams,
                windowManager,
                binding.root
            )
        )
    }

    override fun onCreate() {
        super.onCreate()

        showView()

        deckCardObserver.init(lifecycleScope)


        lifecycleScope.launch {
            deckCardObserver.deckCardList.collect {
//                adapter.setList(it)
                deckAdapter.setDiffNewData(it.toMutableList())
            }
        }

        lifecycleScope.launch {
            deckCardObserver.userGraveyardCardList.collect {
                graveyardAdapter.setDiffNewData(it.toMutableList())
            }
        }

        lifecycleScope.launch {
            deckCardObserver.opponentGraveyardCardList.collect {
                opponentGraveyardAdapter.setDiffNewData(it.toMutableList())
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        "service 挂了".log()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


}