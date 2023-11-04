package com.f0x1d.logfox.ui.fragment.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.FragmentLogsExtendedCopyBinding
import com.f0x1d.logfox.extensions.views.widgets.setupBackButtonForNavController
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.viewmodel.LogsViewModel
import dev.chrisbanes.insetter.applyInsetter

class LogsExtendedCopyFragment: BaseViewModelFragment<LogsViewModel, FragmentLogsExtendedCopyBinding>() {

    override val viewModel by hiltNavGraphViewModels<LogsViewModel>(R.id.logsFragment)

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLogsExtendedCopyBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollView.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }
        binding.toolbar.setupBackButtonForNavController()

        binding.logText.text = viewModel.selectedItemsContent
    }
}