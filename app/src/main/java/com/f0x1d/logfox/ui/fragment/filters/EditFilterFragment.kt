package com.f0x1d.logfox.ui.fragment.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.FragmentEditFilterBinding
import com.f0x1d.logfox.extensions.applyInsets
import com.f0x1d.logfox.extensions.setClickListenerOn
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.utils.dpToPx
import com.f0x1d.logfox.viewmodel.filters.FilterTextData
import com.f0x1d.logfox.viewmodel.filters.FilterViewModel
import com.f0x1d.logfox.viewmodel.filters.FilterViewModelAssistedFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditFilterFragment: BaseViewModelFragment<FilterViewModel, FragmentEditFilterBinding>() {

    @Inject
    lateinit var assistedFactory: FilterViewModelAssistedFactory

    override val viewModel by viewModels<FilterViewModel> {
        viewModelFactory {
            initializer {
                assistedFactory.create(navArgs.filterId)
            }
        }
    }

    private val exportFilterLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
        viewModel.export(it ?: return@registerForActivityResult)
    }

    private val navArgs by navArgs<EditFilterFragmentArgs>()

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

        binding.logLevelsButton.setOnClickListener {
            showFilterDialog()
        }

        binding.saveFab.setOnClickListener {
            viewModel.create(collectFilterTextData())
            findNavController().popBackStack()
        }

        viewModel.filter.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            binding.pidText.setText(it.pid)
            binding.tidText.setText(it.tid)
            binding.tagText.setText(it.tag)
            binding.contentText.setText(it.content)

            binding.toolbar.inflateMenu(R.menu.edit_filter_menu)
            binding.toolbar.menu.setClickListenerOn(R.id.export_item) {
                exportFilterLauncher.launch("filter.json")
            }

            binding.saveFab.setOnClickListener { view ->
                viewModel.update(it, collectFilterTextData())
                findNavController().popBackStack()
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