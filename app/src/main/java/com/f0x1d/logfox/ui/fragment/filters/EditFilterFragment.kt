package com.f0x1d.logfox.ui.fragment.filters

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.FragmentEditFilterBinding
import com.f0x1d.logfox.extensions.applyInsets
import com.f0x1d.logfox.extensions.setClickListenerOn
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.dpToPx
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.filters.EditFilterViewModel
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class EditFilterFragment: BaseViewModelFragment<EditFilterViewModel, FragmentEditFilterBinding>() {

    override val viewModel by hiltNavGraphViewModels<EditFilterViewModel>(R.id.editFilterFragment)

    private val exportFilterLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
        viewModel.export(it ?: return@registerForActivityResult)
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentEditFilterBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyInsets(view) { insets ->
            binding.saveFab.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = 10.dpToPx.toInt() + insets.bottom
            }

            binding.scrollView.updatePadding(bottom = 71.dpToPx.toInt() + insets.bottom)
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.includingButton.setOnClickListener {
            viewModel.including.update { !it }
        }
        binding.logLevelsButton.setOnClickListener {
            showFilterDialog()
        }

        binding.selectAppButton.setOnClickListener {
            findNavController().navigate(
                EditFilterFragmentDirections.actionEditFilterFragmentToChooseAppFragment()
            )
        }

        binding.saveFab.setOnClickListener {
            viewModel.create()
            findNavController().popBackStack()
        }

        viewModel.filter.observe(viewLifecycleOwner) {
            viewModel.including.asLiveData().observe(viewLifecycleOwner) { enabled ->
                updateIncludingButton(enabled)
            }

            viewModel.uid.toText(binding.uidText)
            viewModel.pid.toText(binding.pidText)
            viewModel.tid.toText(binding.tidText)
            viewModel.packageName.toText(binding.packageNameText)
            viewModel.tag.toText(binding.tagText)
            viewModel.content.toText(binding.contentText)

            if (it == null) return@observe

            binding.toolbar.inflateMenu(R.menu.edit_filter_menu)
            binding.toolbar.menu.setClickListenerOn(R.id.export_item) {
                exportFilterLauncher.launch("filter.json")
            }

            binding.saveFab.setOnClickListener { view ->
                viewModel.update(it)
                findNavController().popBackStack()
            }
        }
    }

    override fun onEvent(event: Event) {
        when (event.type) {
            EditFilterViewModel.EVENT_TYPE_UPDATE_PACKAGE_NAME_TEXT -> {
                binding.packageNameText.setText(viewModel.packageName.value)
            }
        }
    }

    private fun MutableStateFlow<String?>.toText(editText: EditText) {
        take(1).asLiveData().observe(viewLifecycleOwner) {
            editText.apply {
                setText(it)
                doAfterTextChanged { value -> update { value?.toString() } }
            }
        }
    }

    private fun updateIncludingButton(enabled: Boolean) = binding.includingButton.run {
        setIconResource(if (enabled) R.drawable.ic_add else R.drawable.ic_clear)

        ColorStateList.valueOf(MaterialColors.getColor(this, if (enabled)
            android.R.attr.colorPrimary
        else
            androidx.appcompat.R.attr.colorError
        )).also {
            iconTint = it
            strokeColor = it
            setTextColor(it)
        }

        setText(if (enabled) R.string.including else R.string.excluding)
    }

    private fun showFilterDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.log_levels)
            .setIcon(R.drawable.ic_dialog_list)
            .setMultiChoiceItems(LogLevel.values().map { it.name }.toTypedArray(), viewModel.enabledLogLevels.toTypedArray().toBooleanArray()) { dialog, which, checked ->
                viewModel.filterLevel(which, checked)
            }
            .setPositiveButton(R.string.close, null)
            .show()
    }
}