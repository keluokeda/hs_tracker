package com.ke.hs_tracker.module.ui.migrate

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ke.hs_tracker.module.databinding.ModuleActivityMigrateMainBinding

class MigrateMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ModuleActivityMigrateMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.toThisPhone.setOnClickListener {
            startActivity(Intent(this, SocketServerActivity::class.java))
            finish()
        }
        binding.toAnotherPhone.setOnClickListener {
            startActivity(Intent(this, SocketClientActivity::class.java))
            finish()
        }

    }
}

internal fun getLocalIPAddress(context: Context): String {

    val manager =
        context.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
    return int2ip(manager.connectionInfo.ipAddress)
}


internal fun int2ip(ipInt: Int): String {
    val sb = StringBuilder()
    sb.append(ipInt and 0xFF).append(".")
    sb.append(ipInt shr 8 and 0xFF).append(".")
    sb.append(ipInt shr 16 and 0xFF).append(".")
    sb.append(ipInt shr 24 and 0xFF)
    return sb.toString()
}