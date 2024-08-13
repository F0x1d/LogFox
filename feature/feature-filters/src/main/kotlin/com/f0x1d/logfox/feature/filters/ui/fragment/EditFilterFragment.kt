package com.f0x1d.logfox.feature.filters.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.arch.viewmodel.Event
import com.f0x1d.logfox.feature.filters.R
import com.f0x1d.logfox.feature.filters.databinding.FragmentEditFilterBinding
import com.f0x1d.logfox.feature.filters.viewmodel.EditFilterViewModel
import com.f0x1d.logfox.feature.filters.viewmodel.UpdatePackageNameText
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class EditFilterFragment: BaseViewModelFragment<EditFilterViewModel, FragmentEditFilterBinding>() {

    override val viewModel by hiltNavGraphViewModels<EditFilterViewModel>(Directions.editFilterFragment)

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

        includingButton.setOnClickListener {
            viewModel.including.update { !it }
        }
        logLevelsButton.setOnClickListener {
            showFilterDialog()
        }

        selectAppButton.setOnClickListener {
            findNavController().navigate(
                Directions.action_editFilterFragment_to_chooseAppFragment,
            )
        }

        saveFab.setOnClickListener {
            viewModel.create()
            findNavController().popBackStack()
        }

        viewModel.including.collectWithLifecycle { enabled ->
            updateIncludingButton(enabled)
        }

        viewModel.filter.collectWithLifecycle {
            viewModel.uid.toText(uidText)
            viewModel.pid.toText(pidText)
            viewModel.tid.toText(tidText)
            viewModel.packageName.toText(packageNameText)
            viewModel.tag.toText(tagText)
            viewModel.content.toText(contentText)

            if (it == null) return@collectWithLifecycle

            toolbar.menu.apply {
                findItem(R.id.export_item).isVisible = true

                setClickListenerOn(R.id.export_item) {
                    exportFilterLauncher.launch("filter.json")
                }
            }

            saveFab.setOnClickListener { _ ->
                viewModel.update(it)
                findNavController().popBackStack()
            }
        }
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)

        when (event) {
            is UpdatePackageNameText -> {
                binding.packageNameText.setText(viewModel.packageName.value)
            }
        }
    }

    private fun MutableStateFlow<String?>.toText(editText: EditText) {
        take(1).collectWithLifecycle {
            editText.apply {
                setText(it)
                doAfterTextChanged { value -> update { value?.toString() } }
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
                viewModel.enabledLogLevels.toTypedArray().toBooleanArray(),
            ) { _, which, checked ->
                viewModel.filterLevel(which, checked)
            }
            .setPositiveButton(Strings.close, null)
            .show()
    }
}
