package com.ke.hs_tracker.module.ui.zoneevents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.hi.dhl.binding.viewbind
import com.ke.hs_tracker.module.R
import com.ke.hs_tracker.module.databinding.ModuleFragmentListModeBinding
import com.ke.hs_tracker.module.databinding.ModuleItemZoneEventListModeBinding
import com.ke.hs_tracker.module.entity.Zone
import com.ke.hs_tracker.module.entity.ZoneCard
import com.ke.mvvm.base.ui.BaseViewBindingAdapter
import com.ke.mvvm.base.ui.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
internal class ListModeFragment : Fragment() {

    private val onChipClickListener = View.OnClickListener {
        (it.tag as? List<ZoneCard>)?.apply {
            if (isNotEmpty()) {
                (activity as? ZoneEventsActivity)?.toZoneCardsActivity(this)
            }
        }
    }

    private val adapter by lazy {
        object : BaseViewBindingAdapter<GameCardCollections, ModuleItemZoneEventListModeBinding>() {
            override fun bindItem(
                item: GameCardCollections,
                viewBinding: ModuleItemZoneEventListModeBinding,
                viewType: Int,
                position: Int
            ) {
                viewBinding.apply {
                    userDeck.text = "玩家牌库 ${item.userDeckCardList.size}"
                    userHand.text = "玩家手牌 ${item.userHandCardList.size}"
                    userPlay.text = "玩家战场 ${item.userPlayCardList.size}"
                    userGraveyard.text = "玩家墓地 ${item.userGraveyardCardList.size}"
                    userSecret.text = "玩家奥秘 ${item.userSecretCardList.size}"
                    opponentDeck.text = "对手牌库 ${item.opponentDeckCardList.size}"
                    opponentHand.text = "对手手牌 ${item.opponentHandCardList.size}"
                    opponentPlay.text = "对手战场 ${item.opponentPlayCardList.size}"
                    opponentGraveyard.text = "对手墓地 ${item.opponentGraveyardCardList.size}"
                    opponentSecret.text = "对手奥秘 ${item.opponentSecretCardList.size}"

                    userDeck.setOnClickListener(onChipClickListener)
                    userHand.setOnClickListener(onChipClickListener)
                    userPlay.setOnClickListener(onChipClickListener)
                    userGraveyard.setOnClickListener(onChipClickListener)
                    userSecret.setOnClickListener(onChipClickListener)
                    opponentDeck.setOnClickListener(onChipClickListener)
                    opponentHand.setOnClickListener(onChipClickListener)
                    opponentPlay.setOnClickListener(onChipClickListener)
                    opponentGraveyard.setOnClickListener(onChipClickListener)
                    opponentSecret.setOnClickListener(onChipClickListener)

                    userDeck.tag = item.userDeckCardList
                    userHand.tag = item.userHandCardList
                    userPlay.tag = item.userPlayCardList
                    userGraveyard.tag = item.userGraveyardCardList
                    userSecret.tag = item.userSecretCardList
                    opponentDeck.tag = item.opponentDeckCardList
                    opponentHand.tag = item.opponentHandCardList
                    opponentPlay.tag = item.opponentPlayCardList
                    opponentGraveyard.tag = item.opponentGraveyardCardList
                    opponentSecret.tag = item.opponentSecretCardList

//                    userDeck.setOnClickListener {
//                        if (item.userDeckCardList.isNotEmpty()) {
//                            (activity as? ZoneEventsActivity)?.toZoneCardsActivity(item.userDeckCardList)
//                        }
//                    }
                }
            }

            override fun createViewBinding(
                inflater: LayoutInflater,
                parent: ViewGroup,
                viewType: Int
            ): ModuleItemZoneEventListModeBinding {
                return ModuleItemZoneEventListModeBinding.inflate(inflater, parent, false)
            }

        }
    }

    private val binding: ModuleFragmentListModeBinding by viewbind()

    private val zoneEventsViewModel: ZoneEventsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        launchAndRepeatWithViewLifecycle {
            zoneEventsViewModel.collectionsList.collect {
                adapter.setList(it)
            }
        }
    }

}