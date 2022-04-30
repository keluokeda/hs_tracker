package com.ke.hs_tracker.module.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hi.dhl.binding.viewbind
import com.ke.hs_tracker.module.databinding.ModuleFragmentLoadingBinding

class LoadingFragment : Fragment() {
    private val binding:ModuleFragmentLoadingBinding by viewbind()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}