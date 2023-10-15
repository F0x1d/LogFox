package com.f0x1d.logfox.ui.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.databinding.ActivityCrashDetailsBinding
import com.f0x1d.logfox.extensions.copyText
import com.f0x1d.logfox.extensions.crashToZip
import com.f0x1d.logfox.extensions.exportFormatted
import com.f0x1d.logfox.extensions.shareIntent
import com.f0x1d.logfox.extensions.showAreYouSureDialog
import com.f0x1d.logfox.extensions.views.widgets.loadIcon
import com.f0x1d.logfox.extensions.views.replaceAccessibilityDelegateClassNameWithButton
import com.f0x1d.logfox.extensions.views.widgets.setClickListenerOn
import com.f0x1d.logfox.extensions.views.widgets.setupBackButton
import com.f0x1d.logfox.ui.activity.base.BaseViewModelActivity
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.crashes.CrashDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class CrashDetailsActivity: BaseViewModelActivity<CrashDetailsViewModel, ActivityCrashDetailsBinding>() {

    override val viewModel by viewModels<CrashDetailsViewModel>()

    private val zipCrashLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) {
        viewModel.crashToZip(
            it ?: return@registerForActivityResult,
            viewModel.crash.value ?: return@registerForActivityResult
        )
    }

    override fun inflateBinding() = ActivityCrashDetailsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.scrollView.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        binding.toolbar.inflateMenu(R.menu.crash_details_menu)
        binding.toolbar.setupBackButton {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.copyLayout.replaceAccessibilityDelegateClassNameWithButton()
        binding.shareLayout.replaceAccessibilityDelegateClassNameWithButton()
        binding.zipLayout.replaceAccessibilityDelegateClassNameWithButton()

        viewModel.crash.observe(this) {
            setupFor(it ?: return@observe)
        }
    }

    override fun onEvent(event: Event) {
        when (event.type) {
            CrashDetailsViewModel.EVENT_TYPE_COPY_LINK -> {
                copyText(event.consume() ?: return)
                snackbar(R.string.text_copied)
            }
        }
    }

    private fun setupFor(appCrash: AppCrash) {
        binding.toolbar.menu.apply {
            setClickListenerOn(R.id.delete_item) {
                showAreYouSureDialog(R.string.delete, R.string.delete_warning) {
                    viewModel.deleteCrash(appCrash)
                    finish()
                }
            }
        }

        binding.appLogo.loadIcon(appCrash.packageName)
        binding.appName.text = appCrash.appName ?: getString(R.string.unknown)
        binding.appPackage.text = appCrash.packageName

        binding.copyLayout.setOnClickListener {
            copyText(appCrash.log)
            snackbar(R.string.text_copied)
        }

        binding.shareLayout.setOnClickListener {
            shareIntent(appCrash.log)
        }

        binding.zipLayout.setOnClickListener {
            zipCrashLauncher.launch("crash-${appCrash.packageName.replace(".", "-")}-${appCrash.dateAndTime.exportFormatted}.zip")
        }

        binding.logText.text = appCrash.log
    }
}