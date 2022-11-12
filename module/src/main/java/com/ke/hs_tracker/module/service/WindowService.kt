package com.ke.hs_tracker.module.service

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.ke.hs_tracker.module.databinding.ModuleFloatingWindowBinding
import com.ke.hs_tracker.module.domain.GetAllCardUseCase
import com.ke.hs_tracker.module.domain.GetRealLogDirUseCase
import com.ke.hs_tracker.module.domain.ParseDeckCodeUseCase
import com.ke.hs_tracker.module.handler.PowerTagHandler
import com.ke.hs_tracker.module.log
import com.ke.hs_tracker.module.parser.DeckFileObserver
import com.ke.hs_tracker.module.parser.PowerFileObserver
import com.ke.hs_tracker.module.parser.PowerParserImpl
import com.ke.hs_tracker.module.ui.common.CardAdapter
import com.ke.hs_tracker.module.ui.main.powerFileName
import com.ke.mvvm.base.data.successOr
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class WindowService : LifecycleService() {

    @Inject
    lateinit var getLogDirUseCase: GetRealLogDirUseCase


    @Inject
    lateinit var powerTagHandler: PowerTagHandler

    @Inject
    lateinit var getAllCardUseCase: GetAllCardUseCase


    @Inject
    lateinit var powerParser: PowerParserImpl

    @Inject
    lateinit var parseDeckCodeUseCase: ParseDeckCodeUseCase

    private val windowManager: WindowManager by lazy {
        getSystemService(WINDOW_SERVICE) as WindowManager
    }

    private val binding: ModuleFloatingWindowBinding by lazy {
        val layoutInflater = LayoutInflater.from(applicationContext)
        ModuleFloatingWindowBinding.inflate(layoutInflater)
    }

    private val adapter = CardAdapter()


    /**
     * 清空power日志文件
     */
    private suspend fun clearPowerFile() {

        withContext(Dispatchers.IO) {
            val documentFile = getLogsDir()?.findFile(powerFileName)
            documentFile?.apply {
                contentResolver.openOutputStream(uri, "wt")?.use {
                    it.write("".encodeToByteArray())
                    it.flush()
                    it.close()
                }
            }

            powerFileObserver.reset()
            deckFileObserver.reset()
        }


    }


    private val powerFileObserver: PowerFileObserver by lazy {
        PowerFileObserver(1500) {
            getFileStream("Power.log")
        }

    }
    private val deckFileObserver: DeckFileObserver by lazy {
        DeckFileObserver {
            getFileStream("Decks.log")
        }
    }


    private fun showView() {
        val layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams.width = 400
        layoutParams.height = LayoutParams.MATCH_PARENT
        //需要设置 这个 不然空白地方无法点击
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.alpha = 0.8f

        layoutParams.gravity = Gravity.START

        windowManager.addView(binding.root, layoutParams)

        binding.recyclerView.adapter = adapter

        binding.zoom.setOnClickListener {
            binding.recyclerView.isVisible = !binding.recyclerView.isVisible
        }

        binding.close.setOnClickListener {
            windowManager.removeView(binding.root)
            stopSelf()
        }
    }

    override fun onCreate() {
        super.onCreate()

        showView()

        powerParser.powerTagListener = {
//            handlePowerTag(it)
            powerTagHandler.handlePowerTag(it)
        }

        lifecycleScope.launch {
            clearPowerFile()
            powerTagHandler.allCard = getAllCardUseCase(Unit).successOr(emptyList())



            powerTagHandler.deckLeftCardList.collect {

                adapter.setList(it)
                "牌库的卡牌发生了变化 ${it.size} ${
                    it.map { cardBean ->
                        cardBean.card.name to cardBean.count

                    }
                }".log()
            }
        }

        lifecycleScope.launch {
            delay(1000)
            deckFileObserver
                .start()
                .map {
                    it to parseDeckCodeUseCase(it.code).successOr(emptyList())

                }
                .collect {
//                    _deckLeftCardList.value = it.second
//                    _title.value = it.first.name
//                    deckCardList = it.second
//                    currentDeck = it.first

                    powerTagHandler.deckCardList = it.second
                    powerTagHandler.currentDeck = it.first
                }
        }
        lifecycleScope.launch {
            powerFileObserver.start()
                .flowOn(Dispatchers.IO)
                .collect {
                    it.forEach { line ->
                        powerParser.parse(line)
                    }
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

    /**
     * 获取文件流
     */
    private suspend fun getFileStream(fileName: String): InputStream? =
        withContext(Dispatchers.IO) {
            val documentFile = getLogsDir()?.findFile(fileName)
            if (documentFile == null) {
                "无法访问 $fileName 文件".log()

                return@withContext null
            }

            contentResolver.openInputStream(documentFile.uri)
        }

    /**
     * 获取炉石log文件夹
     */
    private suspend fun getLogsDir(): DocumentFile? {
        return getLogDirUseCase(Unit).successOr(null)
    }
}