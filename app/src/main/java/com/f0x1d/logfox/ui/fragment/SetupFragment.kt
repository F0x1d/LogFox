package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.FragmentSetupBinding
import com.f0x1d.logfox.extensions.context.copyText
import com.f0x1d.logfox.extensions.context.hardRestartApp
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.SetupViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment: BaseViewModelFragment<SetupViewModel, FragmentSetupBinding>() {

    override val viewModel by viewModels<SetupViewModel>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSetupBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rootButton.setOnClickListener {
            viewModel.root()
        }
        binding.adbButton.setOnClickListener {
            viewModel.adb()
        }
        binding.shizukuButton.setOnClickListener {
            viewModel.shizuku()
        }
    }

    override fun onEvent(event: Event) {
        when (event.type) {
            SetupViewModel.EVENT_TYPE_GOT_PERMISSION -> requireContext().hardRestartApp()

            SetupViewModel.EVENT_TYPE_SHOW_ADB_DIALOG -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_dialog_adb)
                    .setTitle(R.string.adb)
                    .setMessage(getString(R.string.how_to_use_adb, viewModel.adbCommand))
                    .setPositiveButton(R.string.check) { dialog, which -> viewModel.checkPermission() }
                    .setNeutralButton(android.R.string.copy) { dialog, which ->
                        requireContext().copyText(viewModel.adbCommand)
                        snackbar(R.string.text_copied)
                    }
                    .show()
            }
        }
    }
}