package com.f0x1d.logfox.ui.fragment.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.FragmentLogsExtendedCopyBinding
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import dev.chrisbanes.insetter.applyInsetter

class LogsExtendedCopyFragment: BaseViewModelFragment<com.f0x1d.feature.logging.viewmodel.LogsViewModel, FragmentLogsExtendedCopyBinding>() {

    override val viewModel by hiltNavGraphViewModels<com.f0x1d.feature.logging.viewmodel.LogsViewModel>(R.id.logsFragment)

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
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
