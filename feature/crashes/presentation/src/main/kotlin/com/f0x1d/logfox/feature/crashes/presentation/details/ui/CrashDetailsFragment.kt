package com.f0x1d.logfox.feature.crashes.presentation.details.ui

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
import com.f0x1d.logfox.core.compat.notificationsChannelsAvailable
import com.f0x1d.logfox.core.context.shareIntent
import com.f0x1d.logfox.core.presentation.Colors
import com.f0x1d.logfox.core.presentation.Icons
import com.f0x1d.logfox.core.presentation.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.core.presentation.dialog.showAreYouSureDialog
import com.f0x1d.logfox.core.presentation.view.loadIcon
import com.f0x1d.logfox.core.presentation.view.setClickListenerOn
import com.f0x1d.logfox.core.presentation.view.setupBackButtonForNavController
import com.f0x1d.logfox.core.tea.BaseStoreFragment
import com.f0x1d.logfox.feature.copy.impl.copyText
import com.f0x1d.logfox.feature.crashes.api.data.notificationChannelId
import com.f0x1d.logfox.feature.crashes.presentation.R
import com.f0x1d.logfox.feature.crashes.presentation.databinding.FragmentCrashDetailsBinding
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsCommand
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsSideEffect
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsState
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsViewModel
import com.f0x1d.logfox.feature.database.model.AppCrash
import com.f0x1d.logfox.feature.strings.Strings
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import java.util.Locale

@AndroidEntryPoint
internal class CrashDetailsFragment :
    BaseStoreFragment<
        FragmentCrashDetailsBinding,
        CrashDetailsState,
        CrashDetailsCommand,
        CrashDetailsSideEffect,
        CrashDetailsViewModel,
        >() {

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

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCrashDetailsBinding.inflate(inflater, container, false)

    override fun FragmentCrashDetailsBinding.onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        scrollView.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        toolbar.setupBackButtonForNavController()
        toolbar.menu.apply {
            setClickListenerOn(R.id.info_item) {
                viewModel.state.value.crash?.let { appCrash ->
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", appCrash.packageName, null)
                    }.let(::startActivity)
                }
            }
            setClickListenerOn(R.id.notifications_item) {
                viewModel.state.value.crash?.let { appCrash ->
                    Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                        putExtra(Settings.EXTRA_CHANNEL_ID, appCrash.notificationChannelId)
                    }.let(::startActivity)
                }
            }
            setClickListenerOn(R.id.blacklist_item) {
                viewModel.state.value.crash?.let { appCrash ->
                    if (viewModel.state.value.blacklisted == false) {
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
                    viewModel.state.value.crash?.let(viewModel::deleteCrash)
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
            },
        )
        (searchItem.actionView as SearchView).setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchInLog(newText ?: return false)
                    return true
                }
            },
        )

        copyButton.setOnClickListener {
            requireContext().copyText(viewModel.state.value.crashLog.orEmpty())
            snackbar(Strings.text_copied)
        }

        shareButton.setOnClickListener {
            requireContext().shareIntent(viewModel.state.value.crashLog.orEmpty())
        }

        zipButton.setOnClickListener {
            viewModel.state.value.crash?.let { appCrash ->
                val pkg = appCrash.packageName.replace(".", "-")
                val formattedDate = viewModel.formatForExport(appCrash.dateAndTime)

                zipCrashLauncher.launch("crash-$pkg-$formattedDate.zip")
            }
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, closeSearchOnBackPressedCallback)
        }
    }

    override fun render(state: CrashDetailsState) {
        binding.toolbar.menu.findItem(R.id.notifications_item).isVisible =
            notificationsChannelsAvailable && state.useSeparateNotificationsChannelsForCrashes

        state.crash?.let { binding.setupFor(it, state.crashLog, state.wrapCrashLogLines) }

        binding.toolbar.menu.findItem(R.id.blacklist_item).apply {
            if (state.blacklisted == null) {
                isVisible = false
            } else {
                isVisible = true
                setIcon(if (state.blacklisted) Icons.ic_check_circle else Icons.ic_block)
                setTitle(
                    if (state.blacklisted) Strings.remove_from_blacklist else Strings.add_to_blacklist,
                )
            }
        }
    }

    override fun handleSideEffect(sideEffect: CrashDetailsSideEffect) {
        // Business logic side effects are handled by EffectHandler
        // UI side effects would be handled here
    }

    @SuppressLint("InlinedApi")
    private fun FragmentCrashDetailsBinding.setupFor(
        appCrash: AppCrash,
        crashLog: String?,
        wrapCrashLogLines: Boolean,
    ) {
        appLogo.loadIcon(appCrash.packageName)
        appName.text = appCrash.appName ?: getString(Strings.unknown)
        appPackage.text = appCrash.packageName

        logText.isVisible = wrapCrashLogLines
        logTextScrollableContainer.isVisible = wrapCrashLogLines.not()

        logText.text = crashLog
        logTextScrollable.text = crashLog
    }

    private fun FragmentCrashDetailsBinding.searchInLog(text: String) {
        var stackTrace = viewModel.state.value.crashLog ?: return
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
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
                index += size
            }
        }
        logText.setText(span, TextView.BufferType.SPANNABLE)
        logTextScrollable.setText(span, TextView.BufferType.SPANNABLE)
    }

    private val FragmentCrashDetailsBinding.searchItem get() =
        toolbar.menu.findItem(R.id.search_item)

    private fun snackbar(messageRes: Int) {
        Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_SHORT).show()
    }
}
