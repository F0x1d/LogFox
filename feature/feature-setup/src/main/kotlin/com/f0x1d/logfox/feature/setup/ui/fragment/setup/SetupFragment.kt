package com.f0x1d.logfox.feature.setup.ui.fragment.setup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.arch.ui.fragment.compose.BaseComposeViewModelFragment
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupScreenContent
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupScreenListener
import com.f0x1d.logfox.feature.setup.viewmodel.SetupViewModel
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment: BaseComposeViewModelFragment<SetupViewModel>() {

    override val viewModel by viewModels<SetupViewModel>()

    private val listener by lazy {
        SetupScreenListener(
            onRootClick = viewModel::root,
            onAdbClick = viewModel::adb,
            onShizukuClick = viewModel::shizuku,
            closeAdbDialog = viewModel::closeAdbDialog,
            checkPermission = viewModel::checkPermission,
            copyCommand = viewModel::copyCommand,
        )
    }

    @Composable
    override fun Content() {
        LogFoxTheme {
            val state by viewModel.uiState.collectAsState()

            SetupScreenContent(
                state = state,
                listener = listener,
            )
        }
    }
}
