package com.ke.hs_tracker.module.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleActivityMainBinding
import com.ke.hs_tracker.module.ui.filter.FilterActivity
import com.ke.hs_tracker.module.ui.settings.SettingsActivity
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


//    private val adapter =
//        object : BaseViewBindingAdapter<CardBean, ModuleItemCardBinding>() {
//            override fun bindItem(
//                item: CardBean,
//                viewBinding: ModuleItemCardBinding,
//                viewType: Int,
//                position: Int
//            ) {
//                viewBinding.name.text = item.cardEntity.name + " " + item.count
//                viewBinding.cost.text = item.cardEntity.cost.toString()
//            }
//
//            override fun createViewBinding(
//                inflater: LayoutInflater,
//                parent: ViewGroup,
//                viewType: Int
//            ): ModuleItemCardBinding {
//                return ModuleItemCardBinding.inflate(layoutInflater, parent, false)
//            }
//
//        }

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ModuleActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModuleActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.toolbar.menu.apply {
            clear()
            add(
                0,
                0,
                0,
                R.string.module_settings
            ).setIcon(R.drawable.module_baseline_settings_white_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)


        }
        binding.toolbar.setOnMenuItemClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }

        val titleList = listOf(
            R.string.module_deck,
            R.string.module_graveyard,
            R.string.module_opponent_graveyard
        )

        val fragmentList = listOf<Fragment>(
            DeckCardListFragment(),
            UserGraveyardFragment(),
            OpponentGraveyardFragment()
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
            tab.setText(titleList[index])
        }.attach()

//        val lines = assets
//            .open("Power.log").reader()
//            .readLines()
//            .toMutableList().apply {
//                removeFirstOrNull()
//            }.toList()


//        val mLines = mutableListOf<String>()
//        mLines.addAll(lines)
//
//        val logsDir = getExternalFilesDir("Logs")!!
//        if (logsDir.exists()) {
//            val powerFile = File(logsDir, "Power.log")
//            val writer = FileOutputStream(powerFile, true).writer()
//
//            binding.next.setOnClickListener {
//
//                repeat(100) {
//                    val line = mLines.removeFirstOrNull()
//                    if (line != null) {
//                        writer.append(line)
//                        writer.appendLine()
//                        writer.flush()
//                    }
//                }
//
//            }
//            binding.clear.setOnClickListener {
//                mLines.clear()
//                mLines.addAll(lines)
//            }
//        }


//        binding.recyclerView.adapter = adapter


//        launchAndRepeatWithViewLifecycle {
//            mainViewModel.deckLeftCardList.collect {
//                adapter.setList(it)
//            }
//        }


        launchAndRepeatWithViewLifecycle {
            mainViewModel.title.collect {
                title = it
            }
        }


    }


}