package com.ke.hs_tracker.module.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.tencent.mmkv.MMKV
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface PreferenceStorage {

    var theme: Int

    /**
     * 保存日志文件
     */
    var saveLogFile: Boolean

    var floatingEnable: Boolean

    var crash: String?
}

class PreferenceStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferenceStorage {

    init {
        MMKV.initialize(context)
    }

    private val mmkv = MMKV.defaultMMKV()


    override var theme: Int
        get() = mmkv.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) {
            AppCompatDelegate.setDefaultNightMode(value)
            mmkv.encode(KEY_THEME, value)
        }

    override var saveLogFile: Boolean
        get() = mmkv.getBoolean(KEY_SAVE_LOG_FILE, false)
        set(value) {
            mmkv.encode(KEY_SAVE_LOG_FILE, value)
        }

    override var floatingEnable: Boolean
        get() = mmkv.getBoolean(KEY_FLOATING_ENABLE, true)
        set(value) {
            mmkv.putBoolean(KEY_FLOATING_ENABLE, value)
        }

    override var crash: String?
        get() = mmkv.getString(KEY_CRASH, null)
        set(value) {
            mmkv.putString(KEY_CRASH, value)
        }

    companion object {
        private const val KEY_THEME = "KEY_THEME"
        private const val KEY_SAVE_LOG_FILE = "KEY_SAVE_LOG_FILE"
        private const val KEY_FLOATING_ENABLE = "KEY_FLOATING_ENABLE"
        private const val KEY_CRASH = "KEY_CRASH"
    }
}