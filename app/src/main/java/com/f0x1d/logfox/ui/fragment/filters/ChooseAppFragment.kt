package com.f0x1d.logfox.ui.fragment.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.AppsAdapter
import com.f0x1d.logfox.databinding.FragmentChooseAppBinding
import com.f0x1d.logfox.extensions.dpToPx
import com.f0x1d.logfox.extensions.views.widgets.setClickListenerOn
import com.f0x1d.logfox.extensions.views.widgets.setupBackButtonForNavController
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.viewmodel.filters.ChooseAppViewModel
import com.f0x1d.logfox.viewmodel.filters.EditFilterViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class ChooseAppFragment: BaseViewModelFragment<ChooseAppViewModel, FragmentChooseAppBinding>() {

    override val viewModel by viewModels<ChooseAppViewModel>()

    private val editFilterViewModel by hiltNavGraphViewModels<EditFilterViewModel>(R.id.editFilterFragment)

    private val onAppClicked: (InstalledApp) -> Unit = {
        editFilterViewModel.selectApp(it)
        findNavController().popBackStack()
    }
    private val appsAdapter = AppsAdapter(onAppClicked)
    private val searchedAppsAdapter = AppsAdapter(onAppClicked)

    private val closeSearchOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.searchView.hide()
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentChooseAppBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appsRecycler.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        binding.searchBar.apply {
            setupBackButtonForNavController()
            menu.setClickListenerOn(R.id.search_item) {
                binding.searchView.show()
            }
        }
        binding.searchView.editText.doAfterTextChanged { editable ->
            viewModel.query.update {
                editable.toString()
            }
        }
        binding.searchView.addTransitionListener { searchView, previousState, newState ->
            closeSearchOnBackPressedCallback.isEnabled = newState == SearchView.TransitionState.SHOWN
        }

        listOf(
            binding.appsRecycler,
            binding.searchedAppsRecycler
        ).forEach {
            it.apply {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
                    dividerInsetStart = 80.dpToPx.toInt()
                    dividerInsetEnd = 10.dpToPx.toInt()
                    isLastItemDecorated = false
                })
            }
        }

        binding.appsRecycler.adapter = appsAdapter
        binding.searchedAppsRecycler.adapter = searchedAppsAdapter

        viewModel.apps.asLiveData().observe(viewLifecycleOwner) {
            appsAdapter.submitList(it)
        }
        viewModel.searchedApps.observe(viewLifecycleOwner) {
            searchedAppsAdapter.submitList(it)
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, closeSearchOnBackPressedCallback)
        }
    }
}