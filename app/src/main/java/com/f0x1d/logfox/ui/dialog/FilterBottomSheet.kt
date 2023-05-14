package com.f0x1d.logfox.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.SheetFilterBinding
import com.f0x1d.logfox.extensions.viewModelFactory
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.ui.dialog.base.BaseViewModelBottomSheet
import com.f0x1d.logfox.viewmodel.filters.FilterTextData
import com.f0x1d.logfox.viewmodel.filters.FilterViewModel
import com.f0x1d.logfox.viewmodel.filters.FilterViewModelAssistedFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilterBottomSheet: BaseViewModelBottomSheet<FilterViewModel, SheetFilterBinding>() {

    @Inject
    lateinit var assistedFactory: FilterViewModelAssistedFactory

    override val viewModel by viewModels<FilterViewModel> {
        viewModelFactory {
            assistedFactory.create(navArgs.filterId)
        }
    }

    private val exportFilterLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
        viewModel.export(it ?: return@registerForActivityResult)
    }
    private val navArgs by navArgs<FilterBottomSheetArgs>()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = SheetFilterBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logLevelsButton.setOnClickListener {
            showFilterDialog()
        }

        binding.saveButton.setOnClickListener {
            viewModel.create(collectFilterTextData())
            dismiss()
        }

        viewModel.distinctiveData.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            binding.pidText.setText(it.pid)
            binding.tidText.setText(it.tid)
            binding.tagText.setText(it.tag)
            binding.contentText.setText(it.content)

            binding.exportButton.apply {
                visibility = View.VISIBLE

                setOnClickListener {
                    exportFilterLauncher.launch("filter.json")
                }
            }

            binding.saveButton.setOnClickListener { view ->
                viewModel.update(it, collectFilterTextData())
                dismiss()
            }
        }
    }

    private fun collectFilterTextData() = FilterTextData(
        binding.pidText.text?.toString() ?: "",
        binding.tidText.text?.toString() ?: "",
        binding.tagText.text?.toString() ?: "",
        binding.contentText.text?.toString() ?: ""
    )

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