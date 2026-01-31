package com.f0x1d.logfox.feature.apps.picker.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.feature.apps.picker.AppsPickerResultHandler
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerCommand
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerSideEffect
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerViewModel
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerViewState
import com.f0x1d.logfox.feature.apps.picker.presentation.ui.compose.AppsPickerScreenContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class AppsPickerFragment : Fragment() {

    private val viewModel by viewModels<AppsPickerViewModel>()
    private val resultHandler by resultHandler()

    private val listener by lazy {
        AppsPickerScreenListener(
            onBackClicked = { viewModel.send(AppsPickerCommand.BackPressed) },
            onAppClicked = { viewModel.send(AppsPickerCommand.AppClicked(it)) },
            onAppChecked = { app, checked -> resultHandler?.onAppChecked(app, checked) },
            onSearchActiveChanged = { viewModel.send(AppsPickerCommand.SearchActiveChanged(it)) },
            onQueryChanged = { viewModel.send(AppsPickerCommand.QueryChanged(it)) },
        )
    }

    private val uiState: Flow<AppsPickerViewState> by lazy {
        resultHandler?.let { handler ->
            combine(viewModel.state, handler.checkedAppPackageNames) { state, checkedApps ->
                state to checkedApps
            }.map { (state, checkedAppPackageNames) ->
                state.copy(
                    topBarTitle = handler.providePickerTopAppBarTitle(requireContext()),
                    checkedAppPackageNames = checkedAppPackageNames.toSet(),
                    multiplySelectionEnabled = handler.supportsMultiplySelection,
                )
            }
        } ?: viewModel.state
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            LogFoxTheme {
                FragmentContent()
            }
        }
    }

    @Composable
    private fun FragmentContent() {
        val state by uiState.collectAsStateWithLifecycle(
            initialValue = AppsPickerViewState(
                topBarTitle = "",
                apps = emptyList(),
                checkedAppPackageNames = emptySet(),
                searchedApps = emptyList(),
                multiplySelectionEnabled = true,
                isLoading = true,
                searchActive = false,
                query = "",
            ),
        )

        LaunchedEffect(viewModel) {
            viewModel.sideEffects.collect { sideEffect ->
                handleSideEffect(sideEffect)
            }
        }

        AppsPickerScreenContent(
            state = state,
            listener = listener,
        )
    }

    private fun handleSideEffect(sideEffect: AppsPickerSideEffect) {
        when (sideEffect) {
            is AppsPickerSideEffect.PopBackStack -> {
                findNavController().popBackStack()
            }

            is AppsPickerSideEffect.HandleAppSelection -> {
                if (resultHandler?.onAppSelected(sideEffect.app) == true) {
                    findNavController().popBackStack()
                }
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }

    @SuppressLint("RestrictedApi")
    private fun resultHandler(): Lazy<AppsPickerResultHandler?> = lazy {
        val backStackEntry = findNavController().previousBackStackEntry
            ?: return@lazy null

        val store = backStackEntry.viewModelStore
        val availableViewModelKeys = store.keys()

        availableViewModelKeys
            .firstNotNullOfOrNull { store[it] as? AppsPickerResultHandler }
    }
}
