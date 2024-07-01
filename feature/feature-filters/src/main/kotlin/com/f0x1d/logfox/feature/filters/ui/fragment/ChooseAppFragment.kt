package com.f0x1d.logfox.feature.filters.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.arch.ui.fragment.BaseViewModelFragment
import com.f0x1d.logfox.feature.filters.R
import com.f0x1d.logfox.feature.filters.adapter.AppsAdapter
import com.f0x1d.logfox.feature.filters.databinding.FragmentChooseAppBinding
import com.f0x1d.logfox.feature.filters.viewmodel.ChooseAppViewModel
import com.f0x1d.logfox.feature.filters.viewmodel.EditFilterViewModel
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.navigation.Directions
import com.f0x1d.logfox.ui.density.dpToPx
import com.f0x1d.logfox.ui.view.setClickListenerOn
import com.f0x1d.logfox.ui.view.setupBackButtonForNavController
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class ChooseAppFragment: BaseViewModelFragment<ChooseAppViewModel, FragmentChooseAppBinding>() {

    override val viewModel by viewModels<ChooseAppViewModel>()

    private val editFilterViewModel by hiltNavGraphViewModels<EditFilterViewModel>(Directions.editFilterFragment)

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
        container: ViewGroup?,
    ) = FragmentChooseAppBinding.inflate(inflater, container, false)

    override fun FragmentChooseAppBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        appsRecycler.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }

        searchBar.apply {
            setupBackButtonForNavController()

            menu.setClickListenerOn(R.id.search_item) {
                searchView.show()
            }
        }
        searchView.editText.doAfterTextChanged { editable ->
            viewModel.query.update {
                editable.toString()
            }
        }
        searchView.addTransitionListener { _, _, newState ->
            closeSearchOnBackPressedCallback.isEnabled = newState == SearchView.TransitionState.SHOWN
        }

        listOf(appsRecycler, searchedAppsRecycler).forEach {
            it.apply {
                layoutManager = LinearLayoutManager(requireContext())

                addItemDecoration(
                    MaterialDividerItemDecoration(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                    ).apply {
                        dividerInsetStart = 80.dpToPx.toInt()
                        dividerInsetEnd = 10.dpToPx.toInt()
                        isLastItemDecorated = false
                    }
                )
            }
        }

        appsRecycler.adapter = appsAdapter
        searchedAppsRecycler.adapter = searchedAppsAdapter

        viewModel.apps.collectWithLifecycle {
            appsAdapter.submitList(it)
        }
        viewModel.searchedApps.collectWithLifecycle {
            searchedAppsAdapter.submitList(it)
        }

        requireActivity().onBackPressedDispatcher.apply {
            addCallback(viewLifecycleOwner, closeSearchOnBackPressedCallback)
        }
    }
}
