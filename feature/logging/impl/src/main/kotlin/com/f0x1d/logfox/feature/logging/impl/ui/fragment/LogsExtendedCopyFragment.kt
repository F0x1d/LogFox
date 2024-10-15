package com.f0x1d.logfox.feature.logging.impl.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.feature.logging.impl.databinding.FragmentLogsExtendedCopyBinding
import com.f0x1d.logfox.feature.logging.impl.viewmodel.LogsViewModel
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import dev.chrisbanes.insetter.applyInsetter

class LogsExtendedCopyFragment: BaseViewModelFragment<LogsViewModel, FragmentLogsExtendedCopyBinding>() {

    override val viewModel by hiltNavGraphViewModels<LogsViewModel>(Directions.logsFragment)

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

        logText.text = viewModel.selectedItemsContent
    }
}
