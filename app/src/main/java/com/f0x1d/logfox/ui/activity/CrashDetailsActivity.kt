package com.f0x1d.logfox.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.navArgs
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.databinding.ActivityCrashDetailsBinding
import com.f0x1d.logfox.extensions.*
import com.f0x1d.logfox.ui.activity.base.BaseViewModelActivity
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.CrashDetailsViewModel
import com.f0x1d.logfox.viewmodel.CrashDetailsViewModelAssistedFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CrashDetailsActivity: BaseViewModelActivity<CrashDetailsViewModel, ActivityCrashDetailsBinding>() {

    @Inject
    lateinit var assistedFactory: CrashDetailsViewModelAssistedFactory

    override val viewModel by viewModels<CrashDetailsViewModel> {
        viewModelFactory {
            assistedFactory.create(navArgs.crashId)
        }
    }

    private val zipCrashLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) {
        viewModel.crashToZip(it ?: return@registerForActivityResult) { log }
    }

    private val navArgs by navArgs<CrashDetailsActivityArgs>()

    override fun inflateBinding() = ActivityCrashDetailsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.logCard.applyBottomInsets(window.decorView)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.distinctiveData.observe(this) {
            setupFor(it ?: return@observe)
        }

        viewModel.uploadingStateData.observe(this) {
            binding.foxbinLayout.isEnabled = !it

            (if (it) View.INVISIBLE else View.VISIBLE).also { visibility ->
                binding.foxbinImage.visibility = visibility
                binding.foxbinText.visibility = visibility
            }

            binding.foxbinLoadingProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onEvent(event: Event) {
        if (event.type == CrashDetailsViewModel.EVENT_TYPE_COPY_LINK) {
            copyText(event.consume() ?: return)
            snackbar(R.string.text_copied)
        }
    }

    private fun setupFor(appCrash: AppCrash) {
        val appName = appCrash.appName ?: getString(R.string.unknown)

        binding.toolbar.inflateMenu(R.menu.crash_details_menu)
        binding.toolbar.menu.apply {
            setClickListenerOn(R.id.delete_item) {
                viewModel.deleteCrash(appCrash)
                finish()
            }
        }

        binding.appLogo.loadIcon(appCrash.packageName)
        binding.appName.text = appName
        binding.appPackage.text = appCrash.packageName

        binding.copyLayout.setOnClickListener {
            copyText(appCrash.log)
            snackbar(R.string.text_copied)
        }

        binding.shareLayout.setOnClickListener {
            shareIntent(appCrash.log)
        }

        binding.foxbinLayout.setOnClickListener {
            viewModel.uploadCrash(appCrash.log)
        }

        binding.zipLayout.setOnClickListener {
            zipCrashLauncher.launch("crash-${appCrash.packageName.replace(".", "-")}-${appCrash.dateAndTime.exportFormatted}.zip")
        }

        binding.logText.text = appCrash.log
    }
}