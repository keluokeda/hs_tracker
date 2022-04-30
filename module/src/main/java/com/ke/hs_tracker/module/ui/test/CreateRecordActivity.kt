package com.ke.hs_tracker.module.ui.test

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivityCreateRecordBinding
import com.ke.hs_tracker.module.db.Game
import com.ke.hs_tracker.module.db.GameDao
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.hs_tracker.module.entity.FormatType
import com.ke.hs_tracker.module.entity.GameType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateRecordActivity : AppCompatActivity() {

    @Inject
    lateinit var gameDao: GameDao

    private val heroClasses = CardClass.values().filter {
        it.roundIcon != null
    }

    private var userClass = heroClasses[1]

    private var opponentClass = heroClasses[3]

    lateinit var binding: ModuleActivityCreateRecordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityCreateRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        refreshUserClass()
        refreshOpponentClass()

        binding.titleUserClass.setOnClickListener {
            showClassPickerDialog { dialog, index ->
                dialog.dismiss()
                userClass = heroClasses[index]
                refreshUserClass()
            }
        }

        binding.titleOpponentClass.setOnClickListener {
            showClassPickerDialog { dialog, index ->
                dialog.dismiss()
                opponentClass = heroClasses[index]
                refreshOpponentClass()
            }
        }

        binding.create.setOnClickListener {
            val game = Game(
                buildNumber = binding.buildNumber.text?.toString() ?: "",
                gameType = getGameType(),
                formatType = getFormatType(),
                scenarioID = 2,
                userName = binding.username.text?.toString() ?: "",
                opponentName = binding.opponentName.text?.toString() ?: "",
                isUserFirst = binding.isUserFirst.isChecked,
                isUserWin = binding.isUserWon.isChecked,
                userHero = userClass,
                opponentHero = opponentClass,
                userDeckCode = "AAECAbr5AwSJiwS4oASlrQSEsAQN5boD6LoD77oDm84D8NQDieADiuADpOED0eEDjOQDj+QDr4AEz6wEAA==",
                userDeckName = "天胡",
                startTime = System.currentTimeMillis() - 10000,
                endTime = System.currentTimeMillis()
            )

            lifecycleScope.launch {
                gameDao.insert(game)
                AlertDialog.Builder(this@CreateRecordActivity)
                    .setTitle("提示")
                    .setMessage("写入成功")
                    .setPositiveButton("确定", null)
                    .show()
            }
        }
    }

    private fun showClassPickerDialog(onSelected: (DialogInterface, Int) -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("选择职业")
            .setSingleChoiceItems(heroClasses.map {
                getString(it.titleRes)
            }.toTypedArray(), heroClasses.indexOf(userClass)) { dialog, index ->
                onSelected(dialog, index)
            }.show()
    }

    private fun getGameType(): GameType {
        return if (binding.rbGameType.checkedRadioButtonId == R.id.game_type_ranked) GameType.Ranked else GameType.Casual
    }

    private fun getFormatType(): FormatType {
        return when (binding.rbFormatType.checkedRadioButtonId) {
            R.id.format_type_standard -> FormatType.Standard
            R.id.format_type_classic -> FormatType.Classic
            R.id.format_type_wild -> FormatType.Wild
            else -> FormatType.Unknown
        }
    }

    private fun refreshUserClass() {
        binding.userClass.setText(userClass.titleRes)
    }

    private fun refreshOpponentClass() {
        binding.opponentClass.setText(opponentClass.titleRes)
    }
}