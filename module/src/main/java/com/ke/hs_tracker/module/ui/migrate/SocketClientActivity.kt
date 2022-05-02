package com.ke.hs_tracker.module.ui.migrate

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ke.hs_tracker.module.databinding.ModuleActivitySocketClientBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Socket
import javax.inject.Inject

@AndroidEntryPoint
class SocketClientActivity : AppCompatActivity() {

    @Inject
    internal lateinit var migrateDataConvert: MigrateDataConvert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.module_activity_socket_client)
        val binding: ModuleActivitySocketClientBinding =
            ModuleActivitySocketClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
//        binding.ipAddress.setText(getLocalIPAddress(applicationContext))


        binding.start.setOnClickListener {

            val progressDialog = ProgressDialog(this)
            progressDialog.show()

            lifecycleScope.launch(Dispatchers.IO) {
                val host = binding.ipAddress.text?.toString() ?: return@launch
                val port = binding.ipPort.text?.toString()?.toInt() ?: return@launch

                try {
                    val socket = Socket(host, port)
                    val text = migrateDataConvert.getJsonString()


                    socket.getOutputStream().apply {
                        write(
                            text.toByteArray()
                        )
                        flush()
                        close()
                    }
                    runOnUiThread {
                        progressDialog.dismiss()
                        finish()
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        progressDialog.dismiss()
                    }
                    e.printStackTrace()
                }


            }
        }
    }
}