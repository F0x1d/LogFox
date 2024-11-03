package com.f0x1d.logfox.feature.logging.extended.copy.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.arch.presentation.ui.fragment.BaseFragment
import com.f0x1d.logfox.feature.logging.extended.copy.databinding.FragmentLogsExtendedCopyBinding
import com.f0x1d.logfox.feature.logging.extended.copy.presentation.LogsExtendedCopyViewModel
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class LogsExtendedCopyFragment : BaseFragment<FragmentLogsExtendedCopyBinding>() {

    private val viewModel by viewModels<LogsExtendedCopyViewModel>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentLogsExtendedCopyBinding.inflate(inflater, container, false)

    override fun FragmentLogsExtendedCopyBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        scrollView.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }
        toolbar.setupBackButtonForNavController()

        viewModel.state.collectWithLifecycle { state ->
            logText.text = state.text
        }
    }
}
