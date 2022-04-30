package com.ke.hs_tracker.module.ui.zoneevents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivityZoneEventsBinding
import com.ke.hs_tracker.module.entity.ZoneCard
import com.ke.hs_tracker.module.ui.common.LoadingFragment
import com.ke.hs_tracker.module.ui.zonecards.ZoneCardsActivity
import com.ke.mvvm.base.ui.FragmentViewPager2Adapter
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ZoneEventsActivity : AppCompatActivity() {
    private val zoneEventsViewModel: ZoneEventsViewModel by viewModels()

    private lateinit var binding: ModuleActivityZoneEventsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityZoneEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        val fragmentList = listOf(LoadingFragment(), ListModeFragment(), ListModeFragment())

        //禁止左右滑动
        binding.viewPager.isUserInputEnabled = false

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }
        }

        launchAndRepeatWithViewLifecycle {
            zoneEventsViewModel.currentFragmentIndex.collect {
                binding.toggle.isVisible = it != 0
                binding.viewPager.currentItem = it
            }
        }

    }

    internal fun toZoneCardsActivity(list: List<ZoneCard>) {
        val intent = Intent(this, ZoneCardsActivity::class.java)
        intent.putParcelableArrayListExtra(
            ZoneCardsActivity.EXTRA_KEY_ZONE_CARD_LIST,
            arrayListOf<Parcelable?>().apply {
                addAll(list)
            })
        startActivity(intent)
    }

    companion object {
        const val EXTRA_KEY_ID = "EXTRA_KEY_ID"
    }
}