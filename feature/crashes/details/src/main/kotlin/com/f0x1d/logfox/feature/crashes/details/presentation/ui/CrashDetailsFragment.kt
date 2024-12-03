package com.f0x1d.logfox.feature.crashes.details.presentation.ui

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
import com.f0x1d.logfox.arch.presentation.ui.fragment.BaseFragment
import com.f0x1d.logfox.arch.shareIntent
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.feature.crashes.api.data.notificationChannelId
import com.f0x1d.logfox.feature.crashes.details.R
import com.f0x1d.logfox.feature.crashes.details.databinding.FragmentCrashDetailsBinding
import com.f0x1d.logfox.feature.crashes.details.presentation.CrashDetailsViewModel
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
class CrashDetailsFragment : BaseFragment<FragmentCrashDetailsBinding>() {

    private val viewModel by viewModels<CrashDetailsViewModel>()

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

            setClickListenerOn(R.id.info_item) {
                viewModel.currentState.crash?.let { appCrash ->
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", appCrash.packageName, null)
                    }.let(::startActivity)
                }
            }
            setClickListenerOn(R.id.notifications_item) {
                viewModel.currentState.crash?.let { appCrash ->
                    Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                        putExtra(Settings.EXTRA_CHANNEL_ID, appCrash.notificationChannelId)
                    }.let(::startActivity)
                }
            }
            setClickListenerOn(R.id.blacklist_item) {
                viewModel.currentState.crash?.let { appCrash ->
                    if (viewModel.currentState.blacklisted == false) {
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
            }
            setClickListenerOn(R.id.delete_item) {
                showAreYouSureDeleteDialog {
                    viewModel.currentState.crash?.let(viewModel::deleteCrash)
                    findNavController().popBackStack()
                }
            }
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

        copyButton.setOnClickListener {
            requireContext().copyText(viewModel.currentState.crashLog.orEmpty())
            snackbar(Strings.text_copied)
        }

        shareButton.setOnClickListener {
            requireContext().shareIntent(viewModel.currentState.crashLog.orEmpty())
        }

        zipButton.setOnClickListener {
            viewModel.currentState.crash?.let { appCrash ->
                val pkg = appCrash.packageName.replace(".", "-")
                val formattedDate = viewModel.formatForExport(appCrash.dateAndTime)

                zipCrashLauncher.launch("crash-$pkg-$formattedDate.zip")
            }
        }

        viewModel.state.collectWithLifecycle { state ->
            state.crash?.let { setupFor(it to state.crashLog) }

            toolbar.menu.findItem(R.id.blacklist_item).apply {
                if (state.blacklisted == null) {
                    isVisible = false
                } else {
                    isVisible = true
                    setIcon(if (state.blacklisted) Icons.ic_check_circle else Icons.ic_block)
                    setTitle(if (state.blacklisted) Strings.remove_from_blacklist else Strings.add_to_blacklist)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, closeSearchOnBackPressedCallback)
        }
    }

    @SuppressLint("InlinedApi")
    private fun FragmentCrashDetailsBinding.setupFor(item: Pair<AppCrash, String?>) {
        val (appCrash, crashLog) = item

        appLogo.loadIcon(appCrash.packageName)
        appName.text = appCrash.appName ?: getString(Strings.unknown)
        appPackage.text = appCrash.packageName

        viewModel.wrapCrashLogLines.let { wrap ->
            logText.isVisible = wrap
            logTextScrollableContainer.isVisible = wrap.not()
        }

        logText.text = crashLog
        logTextScrollable.text = crashLog
    }

    private fun FragmentCrashDetailsBinding.searchInLog(text: String) {
        var stackTrace = viewModel.currentState.crashLog ?: return
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

    private val FragmentCrashDetailsBinding.searchItem get() =
        toolbar.menu.findItem(R.id.search_item)
}
