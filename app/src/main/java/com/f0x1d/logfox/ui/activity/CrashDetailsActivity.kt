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
import com.f0x1d.logfox.utils.viewModelFactory
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
        viewModel.logToZip(it ?: return@registerForActivityResult) { log }
    }
    private val navArgs by navArgs<CrashDetailsActivityArgs>()

    override fun inflateBinding() = ActivityCrashDetailsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        viewModel.data.observe(this) {
            setupFor(it ?: return@observe)
        }

        viewModel.uploadingStateData.observe(this) {
            binding.omnibinLayout.isEnabled = !it

            binding.omnibinImage.visibility = if (it) View.INVISIBLE else View.VISIBLE
            binding.omnibinText.visibility = if (it) View.INVISIBLE else View.VISIBLE

            binding.omnibinLoadingProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onEvent(event: Event) {
        if (event.type == CrashDetailsViewModel.EVENT_TYPE_COPY_LINK)
            copyText(event.consume() ?: return)
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
        }

        binding.shareLayout.setOnClickListener {
            shareIntent(appCrash.log)
        }

        binding.omnibinLayout.setOnClickListener {
            appCrash.log.apply {
                if (isOmnibinInstalled())
                    uploadToFoxBin(this)
                else
                    viewModel.uploadCrash(this)
            }
        }
        binding.omnibinText.setText(if (isOmnibinInstalled()) R.string.omnibin else R.string.foxbin)

        binding.zipLayout.setOnClickListener {
            zipCrashLauncher.launch("crash-${appCrash.packageName.replace(".", "-")}-${appCrash.dateAndTime.exportFormatted}.zip")
        }

        binding.logText.text = appCrash.log
    }
}