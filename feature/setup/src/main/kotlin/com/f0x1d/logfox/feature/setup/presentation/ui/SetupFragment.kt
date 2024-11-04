package com.f0x1d.logfox.feature.setup.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.f0x1d.logfox.arch.presentation.ui.fragment.compose.BaseComposeFragment
import com.f0x1d.logfox.feature.setup.presentation.SetupAction
import com.f0x1d.logfox.feature.setup.presentation.SetupViewModel
import com.f0x1d.logfox.feature.setup.presentation.ui.compose.SetupScreenContent
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetupFragment : BaseComposeFragment() {

    private val viewModel by viewModels<SetupViewModel>()

    private val listener: SetupScreenListener by lazy {
        SetupScreenListener(
            onRootClick = viewModel::root,
            onAdbClick = viewModel::adb,
            onShizukuClick = viewModel::shizuku,
            closeAdbDialog = viewModel::closeAdbDialog,
            checkPermission = viewModel::checkPermission,
            copyCommand = viewModel::copyCommand,
        )
    }

    private val snackbarHostState = SnackbarHostState()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.actions.collectWithLifecycle { action ->
            when (action) {
                is SetupAction.ShowSnackbar -> lifecycleScope.launch {
                    snackbarHostState.showSnackbar(getString(action.textResId))
                }
            }
        }
    }

    @Composable
    override fun Content() {
        LogFoxTheme {
            val state by viewModel.state.collectAsState()

            SetupScreenContent(
                state = state,
                listener = listener,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
