package com.f0x1d.logfox.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.f0x1d.logfox.R
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.databinding.ActivityCrashDetailsBinding
import com.f0x1d.logfox.extensions.copyText
import com.f0x1d.logfox.extensions.exportFormatted
import com.f0x1d.logfox.extensions.loadIcon
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
            assistedFactory.create(intent.getLongExtra("crash_id", -1))
        }
    }

    private val zipCrashLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
        it?.apply {
            viewModel.zip(this)
        }
    }

    override fun inflateBinding() = ActivityCrashDetailsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        viewModel.data.observe(this) {
            if (it != null) setupFor(it)
        }
    }

    private fun setupFor(appCrash: AppCrash) {
        val appName = appCrash.appName ?: getString(R.string.unknown)

        binding.toolbar.title = appName
        binding.appLogo.loadIcon(appCrash.packageName)
        binding.appName.text = appName
        binding.appPackage.text = appCrash.packageName

        binding.copyLayout.setOnClickListener {
            copyText(appCrash.log)
        }
        binding.shareLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, appCrash.log)
            intent.type = "text/plain"

            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        }
        binding.zipLayout.setOnClickListener {
            zipCrashLauncher.launch("crash-${appCrash.packageName.replace(".", "-")}-${appCrash.dateAndTime.exportFormatted}.zip")
        }

        binding.logText.text = appCrash.log
    }
}