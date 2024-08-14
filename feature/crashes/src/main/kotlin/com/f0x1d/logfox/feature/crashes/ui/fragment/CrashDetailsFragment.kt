package com.f0x1d.logfox.feature.crashes.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.arch.copyText
import com.f0x1d.logfox.arch.notificationsChannelsAvailable
import com.f0x1d.logfox.arch.shareIntent
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.feature.crashes.R
import com.f0x1d.logfox.feature.crashes.core.controller.notificationChannelId
import com.f0x1d.logfox.feature.crashes.databinding.FragmentCrashDetailsBinding
import com.f0x1d.logfox.feature.crashes.viewmodel.CrashDetailsViewModel
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.ui.view.loadIcon
import com.f0x1d.logfox.ui.view.setClickListenerOn
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class CrashDetailsFragment: BaseViewModelFragment<CrashDetailsViewModel, FragmentCrashDetailsBinding>() {

    override val viewModel by viewModels<CrashDetailsViewModel>()

    private val zipCrashLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip"),
    ) {
        viewModel.exportCrashToZip(it ?: return@registerForActivityResult)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentCrashDetailsBinding.inflate(inflater, container, false)

    override fun FragmentCrashDetailsBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        scrollView.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        toolbar.setupBackButtonForNavController()

        viewModel.crash.collectWithLifecycle {
            setupFor(it ?: return@collectWithLifecycle)
        }
    }

    @SuppressLint("InlinedApi")
    private fun FragmentCrashDetailsBinding.setupFor(item: Pair<AppCrash, String?>) {
        val (appCrash, crashLog) = item

        toolbar.menu.apply {
            setClickListenerOn(R.id.info_item) {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", appCrash.packageName, null)
                }.let(::startActivity)
            }

            findItem(R.id.notifications_item).setVisible(
                notificationsChannelsAvailable
                        && viewModel.useSeparateNotificationsChannelsForCrashes
            )
            setClickListenerOn(R.id.notifications_item) {
                Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, appCrash.notificationChannelId)
                }.let(::startActivity)
            }
            setClickListenerOn(R.id.delete_item) {
                showAreYouSureDeleteDialog {
                    viewModel.deleteCrash(appCrash)
                    findNavController().popBackStack()
                }
            }
        }

        appLogo.loadIcon(appCrash.packageName)
        appName.text = appCrash.appName ?: getString(Strings.unknown)
        appPackage.text = appCrash.packageName

        copyButton.setOnClickListener {
            requireContext().copyText(crashLog ?: "")
            snackbar(Strings.text_copied)
        }

        shareButton.setOnClickListener {
            requireContext().shareIntent(crashLog ?: "")
        }

        zipButton.setOnClickListener {
            val pkg = appCrash.packageName.replace(".", "-")
            val formattedDate = viewModel.formatForExport(appCrash.dateAndTime)
            
            zipCrashLauncher.launch("crash-$pkg-$formattedDate.zip")
        }

        viewModel.wrapCrashLogLines.let { wrap ->
            logText.isVisible = wrap
            logTextScrollableContainer.isVisible = wrap.not()
        }

        logText.text = crashLog
        logTextScrollable.text = crashLog
    }
}
