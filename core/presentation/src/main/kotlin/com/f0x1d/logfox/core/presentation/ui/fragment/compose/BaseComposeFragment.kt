package com.f0x1d.logfox.core.presentation.ui.fragment.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.core.presentation.databinding.FragmentComposeBinding
import com.f0x1d.logfox.core.presentation.ui.fragment.BaseFragment
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

abstract class BaseComposeFragment : BaseFragment<FragmentComposeBinding>() {

    private val dynamicColorAvailabilityProvider: DynamicColorAvailabilityProvider by lazy {
        EntryPointAccessors
            .fromApplication<BaseComposeFragmentEntryPoint>(requireContext())
            .dynamicColorAvailabilityProvider
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentComposeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.setup {
            LogFoxTheme(
                dynamicColor = dynamicColorAvailabilityProvider.isDynamicColorAvailable(),
            ) {
                Content()
            }
        }
    }

    @Composable
    abstract fun Content()

    private fun ComposeView.setup(content: @Composable () -> Unit) {
        consumeWindowInsets = false
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        setContent(content)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface BaseComposeFragmentEntryPoint {
        val dynamicColorAvailabilityProvider: DynamicColorAvailabilityProvider
    }
}
