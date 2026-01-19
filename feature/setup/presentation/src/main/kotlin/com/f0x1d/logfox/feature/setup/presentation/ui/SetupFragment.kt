package com.f0x1d.logfox.feature.setup.presentation.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.f0x1d.logfox.core.context.hardRestartApp
import com.f0x1d.logfox.core.presentation.ui.fragment.compose.BaseComposeFragment
import com.f0x1d.logfox.feature.setup.presentation.SetupCommand
import com.f0x1d.logfox.feature.setup.presentation.SetupSideEffect
import com.f0x1d.logfox.feature.setup.presentation.SetupViewModel
import com.f0x1d.logfox.feature.setup.presentation.ui.compose.SetupScreenContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetupFragment : BaseComposeFragment() {

    private val viewModel by viewModels<SetupViewModel>()

    @Composable
    override fun Content() {
        val state by viewModel.state.collectAsState()
        val context = LocalContext.current
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(lifecycleOwner, viewModel.sideEffects) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffects.collect { sideEffect ->
                    when (sideEffect) {
                        is SetupSideEffect.ShowSnackbar -> scope.launch {
                            snackbarHostState.showSnackbar(context.getString(sideEffect.textResId))
                        }
                        is SetupSideEffect.RestartApp -> context.hardRestartApp()
                        // Business logic side effects - handled by EffectHandler, ignore here
                        else -> Unit
                    }
                }
            }
        }

        val listener = remember {
            SetupScreenListener(
                onRootClick = { viewModel.send(SetupCommand.RootClicked) },
                onAdbClick = { viewModel.send(SetupCommand.AdbClicked) },
                onShizukuClick = { viewModel.send(SetupCommand.ShizukuClicked) },
                closeAdbDialog = { viewModel.send(SetupCommand.CloseAdbDialogClicked) },
                checkPermission = { viewModel.send(SetupCommand.CheckPermissionClicked) },
                copyCommand = { viewModel.send(SetupCommand.CopyCommandClicked) },
            )
        }

        SetupScreenContent(
            state = state,
            listener = listener,
            snackbarHostState = snackbarHostState,
        )
    }
}
