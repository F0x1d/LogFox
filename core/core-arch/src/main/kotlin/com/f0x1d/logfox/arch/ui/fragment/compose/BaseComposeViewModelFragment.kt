package com.f0x1d.logfox.arch.ui.fragment.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.f0x1d.logfox.arch.databinding.FragmentComposeBinding
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.arch.ui.snackbar
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.google.android.material.snackbar.Snackbar
import dev.chrisbanes.insetter.applyInsetter

abstract class BaseComposeViewModelFragment<T : BaseViewModel> : BaseViewModelFragment<T, FragmentComposeBinding>() {

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
                this@BaseComposeViewModelFragment.Content()
            }
        }
    }

    override fun snackbar(text: String): Snackbar = requireView().snackbar(text).apply {
        view.applyInsetter {
            type(navigationBars = true) {
                margin()
            }
        }
    }

    @Composable
    abstract fun Content()
}
