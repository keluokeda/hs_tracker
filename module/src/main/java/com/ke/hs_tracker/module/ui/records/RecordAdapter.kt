package com.ke.hs_tracker.module.ui.records

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleItemRecordBinding
import com.ke.hs_tracker.module.db.Game
import com.ke.mvvm.base.ui.BaseViewBindingAdapter
import java.text.SimpleDateFormat
import java.util.*

class RecordAdapter : BaseViewBindingAdapter<Game, ModuleItemRecordBinding>() {


    override fun bindItem(
        item: Game,
        viewBinding: ModuleItemRecordBinding,
        viewType: Int,
        position: Int
    ) {
        viewBinding.apply {
            userHero.setImageResource(item.userHero!!.roundIcon!!)
            opponentHero.setImageResource(item.opponentHero!!.roundIcon!!)
            date.text = simpleDateFormat.format(Date(item.startTime))
            state.isEnabled = item.isUserWin ?: true
            val type =
                root.context.getString(item.formatType.title) + root.context.getString(item.gameType.title)
            gameType.text = type
            state.setText(if (item.isUserWin == true) R.string.module_win else R.string.module_loss)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ModuleItemRecordBinding {
        return ModuleItemRecordBinding.inflate(inflater, parent, false)
    }
}

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")