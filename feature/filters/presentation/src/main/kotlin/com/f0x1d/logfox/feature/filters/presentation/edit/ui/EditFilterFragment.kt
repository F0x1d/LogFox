package com.f0x1d.logfox.feature.filters.presentation.edit.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreFragment
import com.f0x1d.logfox.core.ui.base.ext.doAfterTextChanged
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.core.ui.view.setClickListenerOn
import com.f0x1d.logfox.core.ui.view.setupBackButtonForNavController
import com.f0x1d.logfox.feature.filters.presentation.R
import com.f0x1d.logfox.feature.filters.presentation.databinding.FragmentEditFilterBinding
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterCommand
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterSideEffect
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterState
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterViewModel
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterViewState
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.navigation.Directions
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        toolbar.setupBackButtonForNavController()
        toolbar.menu.apply {
            setClickListenerOn(R.id.export_item) {
                exportFilterLauncher.launch("filter.json")
            }
        }

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
        contentText.doAfterTextChanged(this@EditFilterFragment) {
            send(EditFilterCommand.UpdateContent(it?.toString().orEmpty()))
        }
    }

    override fun render(state: EditFilterViewState) {
        binding.apply {
            updateIncludingButton(state.including)
            updateEnabledButton(state.enabled)

            setTextIfDifferent(uidText, state.uid.orEmpty())
            setTextIfDifferent(pidText, state.pid.orEmpty())
            setTextIfDifferent(tidText, state.tid.orEmpty())
            setTextIfDifferent(packageNameText, state.packageName.orEmpty())
            setTextIfDifferent(tagText, state.tag.orEmpty())
            setTextIfDifferent(contentText, state.content.orEmpty())

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

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
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
