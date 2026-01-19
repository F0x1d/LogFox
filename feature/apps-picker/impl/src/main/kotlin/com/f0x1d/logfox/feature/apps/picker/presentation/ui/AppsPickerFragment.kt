package com.f0x1d.logfox.feature.apps.picker.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.feature.apps.picker.AppsPickerResultHandler
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerCommand
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerSideEffect
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerState
import com.f0x1d.logfox.feature.apps.picker.presentation.AppsPickerViewModel
import com.f0x1d.logfox.feature.apps.picker.presentation.ui.compose.AppsPickerScreenContent
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toImmutableSet
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
            onAppClicked = {
                if (resultHandler?.onAppSelected(it) == true) {
                    findNavController().popBackStack()
                }
            },
            onAppChecked = { app, checked -> resultHandler?.onAppChecked(app, checked) },
            onSearchActiveChanged = { viewModel.send(AppsPickerCommand.SearchActiveChanged(it)) },
            onQueryChanged = { viewModel.send(AppsPickerCommand.QueryChanged(it)) },
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
        val state by uiState.collectAsStateWithLifecycle(initialValue = AppsPickerState())

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
