package com.f0x1d.logfox.feature.logging.presentation.extended.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.core.ui.view.setupBackButtonForNavController
import com.f0x1d.logfox.core.tea.BaseStoreFragment
import com.f0x1d.logfox.feature.logging.presentation.databinding.FragmentLogsExtendedCopyBinding
import com.f0x1d.logfox.feature.logging.presentation.extended.LogsExtendedCopyCommand
import com.f0x1d.logfox.feature.logging.presentation.extended.LogsExtendedCopySideEffect
import com.f0x1d.logfox.feature.logging.presentation.extended.LogsExtendedCopyState
import com.f0x1d.logfox.feature.logging.presentation.extended.LogsExtendedCopyViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class LogsExtendedCopyFragment :
    BaseStoreFragment<
        FragmentLogsExtendedCopyBinding,
        LogsExtendedCopyState,
        LogsExtendedCopyCommand,
        LogsExtendedCopySideEffect,
        LogsExtendedCopyViewModel,
        >() {

    override val viewModel by viewModels<LogsExtendedCopyViewModel>()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLogsExtendedCopyBinding.inflate(inflater, container, false)

    override fun FragmentLogsExtendedCopyBinding.onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        scrollView.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }
        toolbar.setupBackButtonForNavController()
    }

    override fun render(state: LogsExtendedCopyState) {
        binding.logText.text = state.text
    }

    override fun handleSideEffect(sideEffect: LogsExtendedCopySideEffect) {
        // All side effects are business logic, handled by EffectHandler
        // No UI side effects for this screen
    }
}
