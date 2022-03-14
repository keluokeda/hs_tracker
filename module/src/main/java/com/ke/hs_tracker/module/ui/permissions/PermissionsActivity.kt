package com.ke.hs_tracker.module.ui.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import com.ke.hs_tracker.module.*
import com.ke.hs_tracker.module.databinding.ModuleActivityPermissionsBinding
import com.ke.hs_tracker.module.ui.main.MainActivity
import com.ke.hs_tracker.module.ui.sync.SyncCardDataActivity
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PermissionsActivity : AppCompatActivity() {
    private lateinit var binding: ModuleActivityPermissionsBinding
    private val viewModel: PermissionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.module_set_permissions)


        initView()

        launchAndRepeatWithViewLifecycle {
            viewModel.navigationActions.collect {
                val intent = when (it) {
                    PermissionsNavigationAction.NavigateToMain -> {
                        Intent(this@PermissionsActivity, MainActivity::class.java)
                    }
                    PermissionsNavigationAction.NavigateToSync -> {
                        Intent(this@PermissionsActivity, SyncCardDataActivity::class.java)
                    }
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun initView() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {

            }

        val requestAccessDataDirLauncher = registerForActivityResult(RequestAccessDataDir()) {
            if (it != null) {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }

        binding.step3.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val launcher = registerForActivityResult(RequestManageAllFilesAccessPermission()) {

            }
            binding.step3.setOnClickListener {
                launcher.launch(Unit)
            }
        }

        val onClickListener = View.OnClickListener {
            when (it) {
                binding.step1 -> {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                binding.step2 -> {
                    requestAccessDataDirLauncher.launch(Unit)
                }
                binding.next -> {

                    viewModel.next()

                }
            }
        }

        binding.step1.setOnClickListener(onClickListener)
        binding.step2.setOnClickListener(onClickListener)
        binding.next.setOnClickListener(onClickListener)
    }

    override fun onResume() {
        super.onResume()

        binding.next.isEnabled = hasAllPermissions

        val hasPermission =
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            binding.step1.isEnabled = false
            binding.step1.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.module_baseline_done_green_500_24dp,
                0
            )
        } else {
            binding.step1.isEnabled = true
            binding.step1.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.module_baseline_keyboard_arrow_right_grey_500_24dp,
                0
            )
        }

        if (canReadDataDir) {
            binding.step2.isEnabled = false
            binding.step2.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.module_baseline_done_green_500_24dp,
                0
            )
        } else {
            binding.step2.isEnabled = true
            binding.step2.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.module_baseline_keyboard_arrow_right_grey_500_24dp,
                0
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isExternalStorageManager()) {
                binding.step3.isEnabled = false
                binding.step3.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.module_baseline_done_green_500_24dp,
                    0
                )
            } else {
                binding.step3.isEnabled = true
                binding.step3.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.module_baseline_keyboard_arrow_right_grey_500_24dp,
                    0
                )
            }
        }
    }


}

class RequestAccessDataDir : ActivityResultContract<Unit, Uri?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        val documentFile = DocumentFile.fromTreeUri(context.applicationContext, DATA_DIR_URI)!!
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.uri)

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }

}

@RequiresApi(Build.VERSION_CODES.R)
class RequestManageAllFilesAccessPermission : ActivityResultContract<Unit, Boolean>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:${context.packageName}")
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return Environment.isExternalStorageManager()
    }

}