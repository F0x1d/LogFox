package com.f0x1d.logfox.feature.apps.picker.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.arch.presentation.ui.fragment.compose.BaseComposeFragment
import com.f0x1d.logfox.feature.apps.picker.AppsPickerResultHandler
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerState
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerViewModel
import com.f0x1d.logfox.feature.apps.picker.presentation.ui.compose.AppsPickerScreenContent
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class AppsPickerFragment : BaseComposeFragment() {

    private val viewModel by viewModels<AppsPickerViewModel>()
    private val resultHandler by resultHandler()

    private val listener by lazy {
        AppsPickerScreenListener(
            onBackClicked = {
                viewModel.performBackAction(findNavController()::popBackStack)
            },
            onAppClicked = {
                if (resultHandler?.onAppSelected(it) == true) {
                    findNavController().popBackStack()
                }
            },
            onAppChecked = { app, checked -> resultHandler?.onAppChecked(app, checked) },
            onSearchActiveChanged = viewModel::changeSearchActive,
            onQueryChanged = viewModel::updateQuery,
        )
    }

    private val uiState: Flow<AppsPickerState> by lazy {
        resultHandler?.let { handler ->
            combine(viewModel.state, handler.checkedAppPackageNames) { state, checkedApps ->
                state to checkedApps
            }.map { (state, checkedAppPackageNames) ->
                state.copy(
                    topBarTitle = handler.providePickerTopAppBarTitle(requireContext()),
                    checkedAppPackageNames = checkedAppPackageNames.toImmutableSet(),
                    multiplySelectionEnabled = handler.supportsMultiplySelection,
                )
            }
        } ?: viewModel.state
    }

    @Composable
    override fun Content() {
        LogFoxTheme {
            val state by uiState.collectAsState(initial = viewModel.currentState)

            AppsPickerScreenContent(
                state = state,
                listener = listener,
            )
        }
    }

    @SuppressLint("RestrictedApi")
    private fun Fragment.resultHandler(): Lazy<AppsPickerResultHandler?> = lazy {
        val backStackEntry = findNavController().previousBackStackEntry
            ?: return@lazy null

        val store = backStackEntry.viewModelStore
        val availableViewModelKeys = store.keys()

        availableViewModelKeys
            .firstNotNullOfOrNull { store[it] as? AppsPickerResultHandler }
    }
}
