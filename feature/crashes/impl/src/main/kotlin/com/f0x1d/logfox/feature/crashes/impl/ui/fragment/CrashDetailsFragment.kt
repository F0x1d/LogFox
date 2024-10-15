package com.f0x1d.logfox.feature.crashes.impl.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.arch.copyText
import com.f0x1d.logfox.arch.notificationsChannelsAvailable
import com.f0x1d.logfox.arch.shareIntent
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.feature.crashes.api.controller.notificationChannelId
import com.f0x1d.logfox.feature.crashes.impl.R
import com.f0x1d.logfox.feature.crashes.impl.databinding.FragmentCrashDetailsBinding
import com.f0x1d.logfox.feature.crashes.impl.viewmodel.CrashDetailsViewModel
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Colors
import com.f0x1d.logfox.ui.Icons
import com.f0x1d.logfox.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.ui.dialog.showAreYouSureDialog
import com.f0x1d.logfox.ui.view.loadIcon
import com.f0x1d.logfox.ui.view.setClickListenerOn
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import java.util.Locale

@AndroidEntryPoint
class CrashDetailsFragment: BaseViewModelFragment<CrashDetailsViewModel, FragmentCrashDetailsBinding>() {

    override val viewModel by viewModels<CrashDetailsViewModel>()

    private val zipCrashLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip"),
    ) {
        viewModel.exportCrashToZip(it ?: return@registerForActivityResult)
    }

    private val closeSearchOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.searchItem.collapseActionView()
        }
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
        toolbar.menu.apply {
            findItem(R.id.notifications_item).setVisible(
                notificationsChannelsAvailable
                        && viewModel.useSeparateNotificationsChannelsForCrashes
            )
        }
        searchItem.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    closeSearchOnBackPressedCallback.isEnabled = false
                    return true
                }

                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    closeSearchOnBackPressedCallback.isEnabled = true
                    return true
                }
            }
        )
        (searchItem.actionView as SearchView).setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchInLog(newText ?: return false)
                    return true
                }
            }
        )

        viewModel.crash.collectWithLifecycle {
            setupFor(it ?: return@collectWithLifecycle)
        }

        viewModel.blacklisted.collectWithLifecycle { blacklisted ->
            toolbar.menu.findItem(R.id.blacklist_item).apply {
                if (blacklisted == null) {
                    isVisible = false
                } else {
                    isVisible = true
                    setIcon(if (blacklisted) Icons.ic_check_circle else Icons.ic_block)
                    setTitle(if (blacklisted) Strings.remove_from_blacklist else Strings.add_to_blacklist)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, closeSearchOnBackPressedCallback)
        }
    }

    private fun FragmentCrashDetailsBinding.searchInLog(text: String) {
        var stackTrace = viewModel.crash.value?.second ?: return
        var query = text

        val span = stackTrace.toSpannable()
        if (query.isNotEmpty()) {
            query = query.lowercase(Locale.ENGLISH)
            stackTrace = stackTrace.lowercase(Locale.ENGLISH)

            val size = query.length
            var index = 0
            val highlightColor = requireContext().getColor(Colors.md_theme_primaryContainer)
            while (stackTrace.indexOf(query, index).also { index = it } != -1) {
                span.setSpan(
                    BackgroundColorSpan(highlightColor),
                    index,
                    index + size,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                index += size
            }
        }
        logText.setText(span, TextView.BufferType.SPANNABLE)
        logTextScrollable.setText(span, TextView.BufferType.SPANNABLE)
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
            setClickListenerOn(R.id.notifications_item) {
                Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, appCrash.notificationChannelId)
                }.let(::startActivity)
            }
            setClickListenerOn(R.id.blacklist_item) {
                if (viewModel.blacklisted.value == false) {
                    showAreYouSureDialog(
                        title = Strings.blacklist,
                        message = Strings.warning_blacklist,
                    ) {
                        viewModel.changeBlacklist(appCrash)
                    }
                } else {
                    viewModel.changeBlacklist(appCrash)
                }
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

    private val FragmentCrashDetailsBinding.searchItem get() =
        toolbar.menu.findItem(R.id.search_item)
}
