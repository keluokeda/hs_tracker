package com.ke.hs_tracker.module.ui.migrate

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.ke.hs_tracker.module.databinding.ModuleActivitySocketServerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import javax.inject.Inject

@AndroidEntryPoint
class SocketServerActivity : AppCompatActivity() {


    @Inject
    internal lateinit var migrateDataConvert: MigrateDataConvert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.module_activity_socket_server)
        val binding = ModuleActivitySocketServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.ipAddress.setText(getLocalIPAddress(applicationContext))


        binding.start.setOnClickListener {
            val port = binding.ipPort.text.toString().toIntOrNull() ?: return@setOnClickListener
            it.isEnabled = false
            binding.loadingProgress.isVisible = true
            start(port)
        }
    }

    private fun start(port: Int) {
        val progressDialog = ProgressDialog(this)
        progressDialog.show()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val serverSocket = ServerSocket(port)
                val socket = serverSocket.accept()
                val text = socket.getInputStream().bufferedReader().readLine() ?: ""
//                Logger.d("收到了数据 $text")
                migrateDataConvert.save(text)
                runOnUiThread {
                    finish()
                }
            }

        }
    }
}