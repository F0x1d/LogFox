package com.f0x1d.logfox.ui.activity

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.navArgs
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.databinding.ActivityCrashDetailsBinding
import com.f0x1d.logfox.extensions.applyBottomInsets
import com.f0x1d.logfox.extensions.copyText
import com.f0x1d.logfox.extensions.crashToZip
import com.f0x1d.logfox.extensions.exportFormatted
import com.f0x1d.logfox.extensions.loadIcon
import com.f0x1d.logfox.extensions.setClickListenerOn
import com.f0x1d.logfox.extensions.shareIntent
import com.f0x1d.logfox.ui.activity.base.BaseViewModelActivity
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.crashes.CrashDetailsViewModel
import com.f0x1d.logfox.viewmodel.crashes.CrashDetailsViewModelAssistedFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CrashDetailsActivity: BaseViewModelActivity<CrashDetailsViewModel, ActivityCrashDetailsBinding>() {

    @Inject
    lateinit var assistedFactory: CrashDetailsViewModelAssistedFactory

    override val viewModel by viewModels<CrashDetailsViewModel> {
        viewModelFactory {
            initializer {
                assistedFactory.create(navArgs.crashId)
            }
        }
    }

    private val zipCrashLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) {
        viewModel.crashToZip(
            it ?: return@registerForActivityResult,
            viewModel.crash.value ?: return@registerForActivityResult
        )
    }

    private val navArgs by navArgs<CrashDetailsActivityArgs>()

    override fun inflateBinding() = ActivityCrashDetailsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.logCard.applyBottomInsets(window.decorView)

        binding.toolbar.inflateMenu(R.menu.crash_details_menu)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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
                viewModel.deleteCrash(appCrash)
                finish()
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