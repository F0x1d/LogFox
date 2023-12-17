package com.f0x1d.logfox.ui.fragment.crashes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.databinding.ActivityCrashDetailsBinding
import com.f0x1d.logfox.extensions.context.copyText
import com.f0x1d.logfox.extensions.context.shareIntent
import com.f0x1d.logfox.extensions.showAreYouSureDeleteDialog
import com.f0x1d.logfox.extensions.views.widgets.loadIcon
import com.f0x1d.logfox.extensions.views.widgets.setClickListenerOn
import com.f0x1d.logfox.extensions.views.widgets.setupBackButtonForNavController
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.crashes.CrashDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class CrashDetailsFragment: BaseViewModelFragment<CrashDetailsViewModel, ActivityCrashDetailsBinding>() {

    override val viewModel by viewModels<CrashDetailsViewModel>()

    private val zipCrashLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) {
        viewModel.exportCrashToZip(it ?: return@registerForActivityResult)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ActivityCrashDetailsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollView.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        binding.toolbar.setupBackButtonForNavController()

        viewModel.crash.observe(viewLifecycleOwner) {
            setupFor(it ?: return@observe)
        }
    }

    override fun onEvent(event: Event) {
        when (event.type) {
            CrashDetailsViewModel.EVENT_TYPE_COPY_LINK -> {
                requireContext().copyText(event.consume() ?: return)
                snackbar(R.string.text_copied)
            }
        }
    }

    private fun setupFor(item: Pair<AppCrash, String?>) {
        val (appCrash, crashLog) = item

        binding.toolbar.menu.apply {
            setClickListenerOn(R.id.info_item) {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", appCrash.packageName, null)
                }.also {
                    startActivity(it)
                }
            }
            setClickListenerOn(R.id.delete_item) {
                showAreYouSureDeleteDialog {
                    viewModel.deleteCrash(appCrash)
                    findNavController().popBackStack()
                }
            }
        }

        binding.appLogo.loadIcon(appCrash.packageName)
        binding.appName.text = appCrash.appName ?: getString(R.string.unknown)
        binding.appPackage.text = appCrash.packageName

        binding.copy.setOnClickListener {
            requireContext().copyText(crashLog ?: "")
            snackbar(R.string.text_copied)
        }

        binding.share.setOnClickListener {
            requireContext().shareIntent(crashLog ?: "")
        }

        binding.zip.setOnClickListener {
            zipCrashLauncher.launch("crash-${appCrash.packageName.replace(".", "-")}-${viewModel.dateTimeFormatter.formatForExport(appCrash.dateAndTime)}.zip")
        }

        binding.logText.text = crashLog
    }
}