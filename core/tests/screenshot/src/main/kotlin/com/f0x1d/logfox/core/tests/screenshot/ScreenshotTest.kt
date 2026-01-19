package com.f0x1d.logfox.core.tests.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.f0x1d.logfox.core.tests.compose.onNode
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [34], // https://github.com/takahirom/roborazzi/issues/114
    qualifiers = RobolectricDeviceQualifiers.Pixel7,
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
abstract class ScreenshotTest {

    @JvmField
    @Rule
    val composeRule = createComposeRule()

    @JvmField
    @Rule
    val roborazziRule = RoborazziRule(
        composeRule = composeRule,
        captureRoot = composeRule.onRoot(),
        options = RoborazziRule.Options(
            outputDirectoryPath = SCREENSHOTS_OUTPUT_PATH,
        ),
    )

    protected fun screenshotTestOf(
        actions: SemanticsNodeInteractionsProvider.() -> Unit = { },
        whatToCapture: SemanticsNodeInteractionsProvider.() -> SemanticsNodeInteraction = { onRoot() },
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent(content)

        composeRule.actions()
        waitForIdle()

        composeRule.whatToCapture().captureRoboImage()
    }

    protected fun waitForIdle() = composeRule.waitForIdle()

    protected fun String.node(): SemanticsNodeInteraction = composeRule.onNode(this)

    companion object {
        private const val SCREENSHOTS_OUTPUT_PATH = "src/test/screenshots"
    }
}
