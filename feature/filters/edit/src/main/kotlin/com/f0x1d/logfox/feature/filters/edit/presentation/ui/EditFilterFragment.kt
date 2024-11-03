package com.f0x1d.logfox.feature.filters.edit.presentation.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.arch.presentation.ui.fragment.BaseFragment
import com.f0x1d.logfox.feature.filters.edit.R
import com.f0x1d.logfox.feature.filters.edit.databinding.FragmentEditFilterBinding
import com.f0x1d.logfox.feature.filters.edit.presentation.EditFilterAction
import com.f0x1d.logfox.feature.filters.edit.presentation.EditFilterViewModel
import com.f0x1d.logfox.model.logline.LogLevel
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import com.f0x1d.logfox.ui.view.setClickListenerOn
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class EditFilterFragment : BaseFragment<FragmentEditFilterBinding>() {

    private val viewModel by hiltNavGraphViewModels<EditFilterViewModel>(Directions.editFilterFragment)

    private val exportFilterLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) {
        viewModel.export(it ?: return@registerForActivityResult)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentEditFilterBinding.inflate(inflater, container, false)

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
            viewModel.toggleIncluding()
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
            viewModel.save()
            findNavController().popBackStack()
        }

        uidText.doAfterTextChanged { viewModel.uid = it?.toString().orEmpty() }
        pidText.doAfterTextChanged { viewModel.pid = it?.toString().orEmpty() }
        tidText.doAfterTextChanged { viewModel.tid = it?.toString().orEmpty() }
        packageNameText.doAfterTextChanged { viewModel.packageName = it?.toString().orEmpty() }
        tagText.doAfterTextChanged { viewModel.tag = it?.toString().orEmpty() }
        contentText.doAfterTextChanged { viewModel.tag = it?.toString().orEmpty() }

        viewModel.state.collectWithLifecycle { state ->
            updateIncludingButton(state.including)

            uidText.setText(viewModel.uid.orEmpty())
            pidText.setText(viewModel.pid.orEmpty())
            tidText.setText(viewModel.tid.orEmpty())
            packageNameText.setText(viewModel.packageName.orEmpty())
            tagText.setText(viewModel.tag.orEmpty())
            contentText.setText(viewModel.content.orEmpty())

            toolbar.menu.findItem(R.id.export_item).isVisible = state.filter != null
        }

        viewModel.actions.collectWithLifecycle { action ->
            when (action) {
                is EditFilterAction.UpdatePackageNameText -> {
                    binding.packageNameText.setText(action.packageName)
                }
            }
        }
    }

    private fun FragmentEditFilterBinding.updateIncludingButton(enabled: Boolean) = includingButton.run {
        setIconResource(if (enabled) Icons.ic_add else Icons.ic_clear)

        ColorStateList.valueOf(
            MaterialColors.getColor(
                this,
                if (enabled)
                    android.R.attr.colorPrimary
                else
                    androidx.appcompat.R.attr.colorError
            )
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
                viewModel.currentState.enabledLogLevels.toTypedArray().toBooleanArray(),
            ) { _, which, checked ->
                viewModel.filterLevel(which, checked)
            }
            .setPositiveButton(Strings.close, null)
            .show()
    }
}
