package com.f0x1d.logfox.feature.filters.presentation.edit.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreFragment
import com.f0x1d.logfox.core.ui.base.ext.doAfterTextChanged
import com.f0x1d.logfox.core.ui.dialog.showAreYouSureDialog
import com.f0x1d.logfox.core.ui.dialog.showEditTextDialog
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.core.ui.view.setClickListenerOn
import com.f0x1d.logfox.core.ui.view.setupBackButton
import com.f0x1d.logfox.core.ui.view.setupClickableTitle
import com.f0x1d.logfox.feature.filters.api.model.MatchMode
import com.f0x1d.logfox.feature.filters.presentation.R
import com.f0x1d.logfox.feature.filters.presentation.databinding.FragmentEditFilterBinding
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterCommand
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterSideEffect
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterState
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterViewModel
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterViewState
import com.f0x1d.logfox.feature.filters.presentation.labelRes
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import com.f0x1d.logfox.feature.navigation.api.Directions
import com.f0x1d.logfox.feature.strings.Strings
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class EditFilterFragment :
    BaseStoreFragment<
        FragmentEditFilterBinding,
        EditFilterViewState,
        EditFilterState,
        EditFilterCommand,
        EditFilterSideEffect,
        EditFilterViewModel,
        >() {

    override val viewModel by hiltNavGraphViewModels<EditFilterViewModel>(
        Directions.editFilterFragment,
    )

    private val exportFilterLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        uri?.let { send(EditFilterCommand.Export(it)) }
    }

    // Enabled only while the form has unsaved changes; routes system/predictive back through TEA.
    private val confirmDiscardOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            send(EditFilterCommand.AttemptClose)
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentEditFilterBinding.inflate(inflater, container, false)

    override fun FragmentEditFilterBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        saveFab.applyInsetter {
            type(
                navigationBars = true,
                ime = true,
            ) {
                margin(
                    vertical = true,
                    animated = true,
                )
            }
        }
        scrollView.applyInsetter {
            type(
                navigationBars = true,
                ime = true,
            ) {
                padding(vertical = true)
            }
        }

        toolbar.setupBackButton { send(EditFilterCommand.AttemptClose) }
        toolbar.menu.apply {
            setClickListenerOn(R.id.export_item) {
                exportFilterLauncher.launch("filter.json")
            }
        }
        toolbar.setupClickableTitle(
            background = com.f0x1d.logfox.core.ui.view.R.drawable.bg_toolbar_title_clickable,
        ) { showRenameDialog() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            confirmDiscardOnBackPressedCallback,
        )

        includingButton.setOnClickListener {
            send(EditFilterCommand.ToggleIncluding)
        }
        enabledButton.setOnClickListener {
            send(EditFilterCommand.ToggleEnabled)
        }
        logLevelsButton.setOnClickListener {
            showFilterDialog()
        }

        selectAppButton.setOnClickListener {
            send(EditFilterCommand.SelectApp)
        }

        saveFab.setOnClickListener {
            send(EditFilterCommand.Save)
        }

        uidText.doAfterTextChanged(this@EditFilterFragment) { send(EditFilterCommand.UpdateUid(it?.toString().orEmpty())) }
        pidText.doAfterTextChanged(this@EditFilterFragment) { send(EditFilterCommand.UpdatePid(it?.toString().orEmpty())) }
        tidText.doAfterTextChanged(this@EditFilterFragment) { send(EditFilterCommand.UpdateTid(it?.toString().orEmpty())) }
        packageNameText.doAfterTextChanged(this@EditFilterFragment) {
            send(EditFilterCommand.UpdatePackageName(it?.toString().orEmpty()))
        }
        tagText.doAfterTextChanged(this@EditFilterFragment) { send(EditFilterCommand.UpdateTag(it?.toString().orEmpty())) }
        tagLayout.setEndIconOnClickListener {
            showMatchModeDialog(viewModel.state.value.tagMatchMode) { send(EditFilterCommand.SetTagMatchMode(it)) }
        }
        contentText.doAfterTextChanged(this@EditFilterFragment) {
            send(EditFilterCommand.UpdateContent(it?.toString().orEmpty()))
        }
        contentLayout.setEndIconOnClickListener {
            showMatchModeDialog(viewModel.state.value.contentMatchMode) { send(EditFilterCommand.SetContentMatchMode(it)) }
        }
    }

    override fun render(state: EditFilterViewState) {
        confirmDiscardOnBackPressedCallback.isEnabled = state.isDirty

        binding.apply {
            updateIncludingButton(state.including)
            updateEnabledButton(state.enabled)
            updateTitle(state.name)

            setTextIfDifferent(uidText, state.uid.orEmpty())
            setTextIfDifferent(pidText, state.pid.orEmpty())
            setTextIfDifferent(tidText, state.tid.orEmpty())
            setTextIfDifferent(packageNameText, state.packageName.orEmpty())
            setTextIfDifferent(tagText, state.tag.orEmpty())
            setTextIfDifferent(contentText, state.content.orEmpty())

            updateMatchModeIcon(tagLayout, state.tagMatchMode)
            tagLayout.error = if (state.tagRegexError) getString(Strings.invalid_regex) else null
            updateMatchModeIcon(contentLayout, state.contentMatchMode)
            contentLayout.error = if (state.contentRegexError) getString(Strings.invalid_regex) else null
            saveFab.isEnabled = state.canSave

            toolbar.menu.findItem(R.id.export_item).isVisible = state.filter != null
        }
    }

    override fun handleSideEffect(sideEffect: EditFilterSideEffect) {
        when (sideEffect) {
            is EditFilterSideEffect.NavigateToAppPicker -> {
                findNavController().navigate(Directions.action_editFilterFragment_to_appsPickerFragment)
            }

            is EditFilterSideEffect.Close -> {
                findNavController().popBackStack()
            }

            is EditFilterSideEffect.ConfirmDiscard -> {
                showAreYouSureDialog(
                    title = Strings.discard_changes,
                    message = Strings.discard_changes_message,
                ) {
                    send(EditFilterCommand.AttemptCloseConfirmed)
                }
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }

    private fun FragmentEditFilterBinding.updateTitle(name: String?) = toolbar.run {
        title = name ?: getString(Strings.filter_name_hint)
        setTitleTextColor(
            MaterialColors.getColor(
                this,
                if (name == null) {
                    android.R.attr.textColorHint
                } else {
                    com.google.android.material.R.attr.colorOnSurface
                },
            ),
        )
    }

    private fun FragmentEditFilterBinding.updateIncludingButton(including: Boolean) = includingButton.run {
        setIconResource(if (including) Icons.ic_add else Icons.ic_clear)

        ColorStateList.valueOf(
            MaterialColors.getColor(
                this,
                if (including) {
                    android.R.attr.colorPrimary
                } else {
                    androidx.appcompat.R.attr.colorError
                },
            ),
        ).also {
            iconTint = it
            strokeColor = it
            setTextColor(it)
        }

        setText(if (including) Strings.including else Strings.excluding)
    }

    private fun FragmentEditFilterBinding.updateEnabledButton(enabled: Boolean) = enabledButton.run {
        setIconResource(if (enabled) Icons.ic_eye else Icons.ic_block)

        ColorStateList.valueOf(
            MaterialColors.getColor(
                this,
                if (enabled) {
                    android.R.attr.colorPrimary
                } else {
                    androidx.appcompat.R.attr.colorError
                },
            ),
        ).also {
            iconTint = it
            strokeColor = it
            setTextColor(it)
        }

        setText(if (enabled) Strings.enabled else Strings.disabled)
    }

    private fun showRenameDialog() = requireContext().showEditTextDialog(
        title = getString(Strings.rename_filter),
        initialText = viewModel.state.value.name,
        setupViews = { it.textLayout.setHint(Strings.filter_name) },
        setupDialog = { setIcon(Icons.ic_dialog_text_fields) },
    ) { newName ->
        send(EditFilterCommand.UpdateName(newName.orEmpty()))
    }

    // The end icon shows the active match mode's symbol (~ / .* / =) and opens the mode picker. The
    // mode is conveyed by the symbol itself, so the icon keeps the normal control tint regardless of
    // mode rather than highlighting some modes over others. Shared by the tag and content fields.
    private fun updateMatchModeIcon(layout: TextInputLayout, matchMode: MatchMode) {
        layout.setEndIconDrawable(matchMode.iconRes())
        layout.setEndIconTintList(
            ColorStateList.valueOf(
                MaterialColors.getColor(layout, androidx.appcompat.R.attr.colorControlNormal),
            ),
        )
    }

    private fun MatchMode.iconRes() = when (this) {
        MatchMode.CONTAINS -> Icons.ic_tilde
        MatchMode.REGEX -> Icons.ic_regular_expression
        MatchMode.EXACT -> Icons.ic_equal
    }

    // Lets the user pick how a string field (tag or content) is matched. EXACT is a first-class
    // choice here, not just a migration artifact. Uses a single-choice dialog to match the app's
    // other value pickers (theme, date format, log levels).
    private fun showMatchModeDialog(current: MatchMode, onSelected: (MatchMode) -> Unit) {
        // Item order matches MatchMode.entries, so the checked index is the mode's ordinal.
        val modes = MatchMode.entries
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(Strings.match_mode)
            .setIcon(Icons.ic_call_split)
            .setSingleChoiceItems(
                modes.map { getString(it.labelRes()) }.toTypedArray(),
                modes.indexOf(current),
            ) { dialog, which ->
                onSelected(modes[which])
                dialog.dismiss()
            }
            .setNegativeButton(Strings.close, null)
            .show()
    }

    private fun showFilterDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(Strings.log_levels)
            .setIcon(Icons.ic_dialog_list)
            .setMultiChoiceItems(
                LogLevel.entries.map { it.name }.toTypedArray(),
                viewModel.state.value.enabledLogLevels.toTypedArray().toBooleanArray(),
            ) { _, which, checked ->
                send(EditFilterCommand.FilterLevel(which, checked))
            }
            .setPositiveButton(Strings.close, null)
            .show()
    }

    private fun setTextIfDifferent(textView: android.widget.EditText, text: String) {
        if (textView.text.toString() != text) {
            textView.setText(text)
        }
    }
}
