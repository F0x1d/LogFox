package com.f0x1d.logfox.feature.apps.picker.ui.fragment.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.arch.ui.fragment.compose.BaseComposeViewModelFragment
import com.f0x1d.logfox.feature.apps.picker.ui.fragment.picker.compose.AppsPickerScreenContent
import com.f0x1d.logfox.feature.apps.picker.ui.fragment.picker.compose.AppsPickerScreenListener
import com.f0x1d.logfox.feature.apps.picker.viewmodel.AppsPickerViewModel
import com.f0x1d.logfox.feature.apps.picker.viewmodel.resultHandler
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppsPickerFragment: BaseComposeViewModelFragment<AppsPickerViewModel>() {

    override val viewModel by viewModels<AppsPickerViewModel>()

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
            onSearchActiveChanged = viewModel::changeSearchActive,
            onQueryChanged = viewModel::updateQuery,
        )
    }

    @Composable
    override fun Content() {
        LogFoxTheme {
            val state by viewModel.uiState.collectAsState()

            AppsPickerScreenContent(
                state = state,
                listener = listener,
            )
        }
    }
}
