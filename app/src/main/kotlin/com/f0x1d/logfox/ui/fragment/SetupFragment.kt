package com.f0x1d.logfox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.R
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.context.copyText
import com.f0x1d.logfox.context.hardRestartApp
import com.f0x1d.logfox.databinding.FragmentSetupBinding
import com.f0x1d.logfox.model.event.Event
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

    override fun FragmentSetupBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        rootButton.setOnClickListener {
            viewModel.root()
        }
        adbButton.setOnClickListener {
            viewModel.adb()
        }
        shizukuButton.setOnClickListener {
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
                    .setPositiveButton(R.string.check) { _, _ -> viewModel.checkPermission() }
                    .setNeutralButton(android.R.string.copy) { _, _ ->
                        requireContext().copyText(viewModel.adbCommand)
                        snackbar(R.string.text_copied)
                    }
                    .show()
            }
        }
    }
}
