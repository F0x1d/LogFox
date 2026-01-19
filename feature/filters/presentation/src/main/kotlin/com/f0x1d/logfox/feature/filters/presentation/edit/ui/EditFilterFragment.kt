package com.f0x1d.logfox.feature.filters.presentation.edit.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreFragment
import com.f0x1d.logfox.feature.filters.presentation.R
import com.f0x1d.logfox.feature.filters.presentation.databinding.FragmentEditFilterBinding
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterCommand
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterSideEffect
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterState
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterViewModel
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.core.presentation.Icons
import com.f0x1d.logfox.core.presentation.view.setClickListenerOn
import com.f0x1d.logfox.core.presentation.view.setupBackButtonForNavController
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class EditFilterFragment : BaseStoreFragment<
    FragmentEditFilterBinding,
    EditFilterState,
    EditFilterCommand,
    EditFilterSideEffect,
    EditFilterViewModel,
>() {

    override val viewModel by hiltNavGraphViewModels<EditFilterViewModel>(Directions.editFilterFragment)

    private val exportFilterLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        uri?.let { send(EditFilterCommand.Export(it)) }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentEditFilterBinding.inflate(inflater, container, false)

    override fun FragmentEditFilterBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setupBackButtonForNavController()
        toolbar.menu.apply {
            setClickListenerOn(R.id.export_item) {
                exportFilterLauncher.launch("filter.json")
            }
        }

        includingButton.setOnClickListener {
            send(EditFilterCommand.ToggleIncluding)
        }
        logLevelsButton.setOnClickListener {
            showFilterDialog()
        }

        selectAppButton.setOnClickListener {
            findNavController().navigate(
                Directions.action_editFilterFragment_to_appsPickerFragment,
            )
        }

        saveFab.setOnClickListener {
            send(EditFilterCommand.Save)
            findNavController().popBackStack()
        }

        uidText.doAfterTextChanged { send(EditFilterCommand.UpdateUid(it?.toString().orEmpty())) }
        pidText.doAfterTextChanged { send(EditFilterCommand.UpdatePid(it?.toString().orEmpty())) }
        tidText.doAfterTextChanged { send(EditFilterCommand.UpdateTid(it?.toString().orEmpty())) }
        packageNameText.doAfterTextChanged { send(EditFilterCommand.UpdatePackageName(it?.toString().orEmpty())) }
        tagText.doAfterTextChanged { send(EditFilterCommand.UpdateTag(it?.toString().orEmpty())) }
        contentText.doAfterTextChanged { send(EditFilterCommand.UpdateContent(it?.toString().orEmpty())) }
    }

    override fun render(state: EditFilterState) {
        binding.apply {
            updateIncludingButton(state.including)

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
            is EditFilterSideEffect.UpdatePackageNameField -> {
                binding.packageNameText.setText(sideEffect.packageName)
            }
            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }

    private fun FragmentEditFilterBinding.updateIncludingButton(enabled: Boolean) =
        includingButton.run {
            setIconResource(if (enabled) Icons.ic_add else Icons.ic_clear)

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

            setText(if (enabled) Strings.including else Strings.excluding)
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

    private fun setTextIfDifferent(
        textView: android.widget.EditText,
        text: String,
    ) {
        if (textView.text.toString() != text) {
            textView.setText(text)
        }
    }
}
