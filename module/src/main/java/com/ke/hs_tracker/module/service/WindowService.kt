package com.ke.hs_tracker.module.service

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
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

    private val adapter = CardAdapter()


    @Inject
    lateinit var deckCardObserver: DeckCardObserver


    private fun showView() {
        val layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams.width = 400
        layoutParams.height = LayoutParams.WRAP_CONTENT
        //需要设置 这个 不然空白地方无法点击
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//        layoutParams.alpha = 0.8f

        layoutParams.gravity = Gravity.START or Gravity.TOP

        windowManager.addView(binding.root, layoutParams)

        binding.recyclerView.adapter = adapter

        binding.zoom.setOnClickListener {
            binding.recyclerView.isVisible = !binding.recyclerView.isVisible
        }

        binding.close.setOnClickListener {
            windowManager.removeView(binding.root)
            stopSelf()
        }

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
                adapter.setDiffNewData(it.toMutableList())
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