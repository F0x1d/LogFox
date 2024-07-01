package com.f0x1d.logfox.feature.setup.ui.fragment.setup

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.arch.ui.fragment.compose.BaseComposeViewModelFragment
import com.f0x1d.logfox.feature.setup.viewmodel.SetupViewModel
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment: BaseComposeViewModelFragment<SetupViewModel>() {

    override val viewModel by viewModels<SetupViewModel>()

    @Composable
    override fun Content() {
        LogFoxTheme {
            SetupScreenContent(
                viewModel = viewModel,
            )
        }
    }
}
