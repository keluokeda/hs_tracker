package com.ke.hs_tracker.module.ui.deckbattledetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivityDeckBattleDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeckBattleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ModuleActivityDeckBattleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        val fragmentList = listOf(
            SummaryFragment(),
            BattleRecordsFragment(),
            DeckFragment(),
            DeckDetailFragment()
        )
        val titles = listOf(
            getString(R.string.module_summary),
            getString(R.string.module_record),
            getString(R.string.module_deck),
            getString(
                R.string.module_deck_detail
            )
        )
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }
        }
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, index ->
            tab.text = titles[index]
        }.attach()
    }


    companion object {
        fun createIntent(context: Context, deckCode: String, deckName: String): Intent {
            return Intent(context, DeckBattleDetailActivity::class.java).apply {
                putExtra(EXTRA_DECK_NAME, deckName)
                putExtra(EXTRA_DECK_CODE, deckCode)
            }
        }

        internal const val EXTRA_DECK_CODE = "EXTRA_DECK_CODE"
        internal const val EXTRA_DECK_NAME = "EXTRA_DECK_NAME"

    }
}