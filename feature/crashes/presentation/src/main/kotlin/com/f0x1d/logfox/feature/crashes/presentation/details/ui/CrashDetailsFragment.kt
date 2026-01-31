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
import com.f0x1d.logfox.core.copy.copyText
import com.f0x1d.logfox.core.tea.BaseStoreFragment
import com.f0x1d.logfox.core.ui.dialog.showAreYouSureDeleteDialog
import com.f0x1d.logfox.core.ui.dialog.showAreYouSureDialog
import com.f0x1d.logfox.core.ui.glide.loadIcon
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.core.ui.theme.Colors
import com.f0x1d.logfox.core.ui.view.setClickListenerOn
import com.f0x1d.logfox.core.ui.view.setupBackButtonForNavController
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import com.f0x1d.logfox.feature.crashes.presentation.R
import com.f0x1d.logfox.feature.crashes.presentation.databinding.FragmentCrashDetailsBinding
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsCommand
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsSideEffect
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsState
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsViewModel
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsViewState
import com.f0x1d.logfox.feature.strings.Strings
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class CrashDetailsFragment :
    BaseStoreFragment<
        FragmentCrashDetailsBinding,
        CrashDetailsViewState,
        CrashDetailsState,
        CrashDetailsCommand,
        CrashDetailsSideEffect,
        CrashDetailsViewModel,
        >() {

    override val viewModel by viewModels<CrashDetailsViewModel>()

    private val zipCrashLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip"),
    ) {
        it?.let { uri -> send(CrashDetailsCommand.ExportCrashToZip(uri)) }
    }

    // no plain because android will append .txt itself
    private val exportCrashLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/*"),
    ) {
        it?.let { uri -> send(CrashDetailsCommand.ExportCrashToFile(uri)) }
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
                send(CrashDetailsCommand.OpenAppInfoClicked)
            }
            setClickListenerOn(R.id.notifications_item) {
                send(CrashDetailsCommand.OpenNotificationSettingsClicked)
            }
            setClickListenerOn(R.id.blacklist_item) {
                send(CrashDetailsCommand.BlacklistClicked)
            }
            setClickListenerOn(R.id.delete_item) {
                send(CrashDetailsCommand.DeleteClicked)
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
                    send(CrashDetailsCommand.SearchInLog(newText ?: return false))
                    return true
                }
            },
        )

        copyButton.setOnClickListener {
            send(CrashDetailsCommand.CopyCrashLog)
        }

        shareButton.setOnClickListener {
            send(CrashDetailsCommand.ShareCrashLog)
        }

        exportButton.setOnClickListener {
            send(CrashDetailsCommand.ExportCrashToFileClicked)
        }

        zipButton.setOnClickListener {
            send(CrashDetailsCommand.ExportCrashToZipClicked)
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, closeSearchOnBackPressedCallback)
        }
    }

    override fun render(state: CrashDetailsViewState) {
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

        state.crashLog?.let { log ->
            if (state.searchMatchRanges.isNotEmpty() || state.searchQuery.isNotEmpty()) {
                val span = log.toSpannable()
                val highlightColor = requireContext().getColor(Colors.md_theme_primaryContainer)
                state.searchMatchRanges.forEach { range ->
                    span.setSpan(
                        BackgroundColorSpan(highlightColor),
                        range.first,
                        range.last + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                }
                binding.logText.setText(span, TextView.BufferType.SPANNABLE)
                binding.logTextScrollable.setText(span, TextView.BufferType.SPANNABLE)
            }
        }
    }

    @SuppressLint("InlinedApi")
    override fun handleSideEffect(sideEffect: CrashDetailsSideEffect) {
        when (sideEffect) {
            is CrashDetailsSideEffect.OpenAppInfo -> {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", sideEffect.packageName, null)
                }.let(::startActivity)
            }

            is CrashDetailsSideEffect.OpenNotificationSettings -> {
                Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, sideEffect.channelId)
                }.let(::startActivity)
            }

            is CrashDetailsSideEffect.ConfirmBlacklist -> {
                showAreYouSureDialog(
                    title = Strings.blacklist,
                    message = Strings.warning_blacklist,
                ) {
                    send(CrashDetailsCommand.ConfirmBlacklist)
                }
            }

            is CrashDetailsSideEffect.ConfirmDelete -> {
                showAreYouSureDeleteDialog {
                    send(CrashDetailsCommand.ConfirmDelete)
                }
            }

            is CrashDetailsSideEffect.CopyText -> {
                requireContext().copyText(sideEffect.text)
                snackbar(Strings.text_copied)
            }

            is CrashDetailsSideEffect.ShareCrashLog -> {
                requireContext().shareIntent(sideEffect.text)
            }

            is CrashDetailsSideEffect.Close -> {
                findNavController().popBackStack()
            }

            is CrashDetailsSideEffect.LaunchFileExportPicker -> {
                exportCrashLauncher.launch(sideEffect.filename)
            }

            is CrashDetailsSideEffect.LaunchZipExportPicker -> {
                zipCrashLauncher.launch(sideEffect.filename)
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }

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

    private val FragmentCrashDetailsBinding.searchItem get() =
        toolbar.menu.findItem(R.id.search_item)

    private fun snackbar(messageRes: Int) {
        Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_SHORT).show()
    }
}
