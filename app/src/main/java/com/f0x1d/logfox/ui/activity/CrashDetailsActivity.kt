package com.f0x1d.logfox.ui.activity

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.navArgs
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.databinding.ActivityCrashDetailsBinding
import com.f0x1d.logfox.extensions.*
import com.f0x1d.logfox.ui.activity.base.BaseViewModelActivity
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

    private val zipCrashLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
        it?.apply {
            viewModel.logToZip(this) { log }
        }
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
    }

    private fun setupFor(appCrash: AppCrash) {
        val appName = appCrash.appName ?: getString(R.string.unknown)

        binding.appLogo.loadIcon(appCrash.packageName)
        binding.appName.text = appName
        binding.appPackage.text = appCrash.packageName

        binding.copyLayout.setOnClickListener {
            copyText(appCrash.log)
        }
        isOmnibinInstalled().also {
            binding.shareImage.setImageResource(if (it) R.drawable.ic_add_link else R.drawable.ic_share)
            binding.shareText.setText(if (it) R.string.omnibin else R.string.share)

            binding.shareLayout.setOnClickListener { view ->
                appCrash.log.apply {
                    if (it)
                        uploadToFoxBin(this)
                    else
                        shareIntent(this)
                }
            }
        }
        binding.zipLayout.setOnClickListener {
            zipCrashLauncher.launch("crash-${appCrash.packageName.replace(".", "-")}-${appCrash.dateAndTime.exportFormatted}.zip")
        }

        binding.logText.text = appCrash.log
    }
}