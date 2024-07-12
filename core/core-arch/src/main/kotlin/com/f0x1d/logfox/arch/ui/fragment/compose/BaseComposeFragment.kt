package com.f0x1d.logfox.arch.ui.fragment.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.f0x1d.logfox.arch.databinding.FragmentComposeBinding
import com.f0x1d.logfox.arch.ui.fragment.BaseFragment

abstract class BaseComposeFragment : BaseFragment<FragmentComposeBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentComposeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.composeView.apply {
            consumeWindowInsets = false
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                this@BaseComposeFragment.Content()
            }
        }
    }

    @Composable
    abstract fun Content()
}
