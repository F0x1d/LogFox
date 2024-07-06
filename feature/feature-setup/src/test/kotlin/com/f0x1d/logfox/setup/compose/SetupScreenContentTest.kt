package com.f0x1d.logfox.setup.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.f0x1d.logfox.core.tests.ScreenshotTest
import com.f0x1d.logfox.core.tests.compose.clickOn
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.MockSetupScreenListener
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupAdbButtonTestTag
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupAdbDialogTestTag
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupScreenContent
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupScreenState
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme
import org.junit.Test

class SetupScreenContentTest : ScreenshotTest() {

    @Test
    fun shouldShowSetupScreenContent() = screenshotTestOf {
        LogFoxTheme {
            SetupScreenContent()
        }
    }

    @Test
    fun shouldShowAdbDialogOnSetupScreenContent() = screenshotTestOf(
        whatToCapture = { SetupAdbDialogTestTag.node() },
    ) {
        LogFoxTheme {
            SetupScreenContent(
                state = SetupScreenState(showAdbDialog = true),
            )
        }
    }

    @Test
    fun shouldOpenAdbDialogOnSetupScreenContent() = screenshotTestOf(
        actions = { clickOn(SetupAdbButtonTestTag) },
        whatToCapture = { SetupAdbDialogTestTag.node() },
    ) {
        var state by remember {
            mutableStateOf(SetupScreenState())
        }

        LogFoxTheme {
            SetupScreenContent(
                state = state,
                listener = MockSetupScreenListener.copy(
                    onAdbClick = { state = state.copy(showAdbDialog = true) },
                ),
            )
        }
    }
}
