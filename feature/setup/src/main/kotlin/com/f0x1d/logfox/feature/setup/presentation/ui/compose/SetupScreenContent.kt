package com.f0x1d.logfox.feature.setup.presentation.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.feature.setup.presentation.SetupState
import com.f0x1d.logfox.feature.setup.presentation.ui.MockSetupScreenListener
import com.f0x1d.logfox.feature.setup.presentation.ui.SetupScreenListener
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import com.f0x1d.logfox.ui.compose.component.button.RichButton
import com.f0x1d.logfox.ui.compose.preview.DayNightPreview
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SetupScreenContent(
    state: SetupState = SetupState(),
    listener: SetupScreenListener = MockSetupScreenListener,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = Strings.setup)) },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RichButton(
                    text = { Text(text = stringResource(id = Strings.root)) },
                    icon = {
                        Icon(
                            painter = painterResource(id = Icons.ic_square_root),
                            contentDescription = null,
                        )
                    },
                    onClick = listener.onRootClick,
                )

                RichButton(
                    modifier = Modifier.testTag(SetupAdbButtonTestTag),
                    text = { Text(text = stringResource(id = Strings.adb)) },
                    icon = {
                        Icon(
                            painter = painterResource(id = Icons.ic_adb),
                            contentDescription = null,
                        )
                    },
                    onClick = listener.onAdbClick,
                )

                RichButton(
                    text = { Text(text = stringResource(id = Strings.shizuku)) },
                    icon = {
                        Icon(
                            painter = painterResource(id = Icons.ic_terminal),
                            contentDescription = null,
                        )
                    },
                    onClick = listener.onShizukuClick,
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = stringResource(id = Strings.logs_restart_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }

    if (state.showAdbDialog) {
        AdbDialog(
            message = stringResource(
                id = Strings.how_to_use_adb,
                state.adbCommand,
            ),
            onDismissed = listener.closeAdbDialog,
            checkPermission = listener.checkPermission,
            copyCommand = listener.copyCommand,
        )
    }
}

@Composable
private fun AdbDialog(
    modifier: Modifier = Modifier,
    message: String = "",
    onDismissed: () -> Unit = { },
    checkPermission: () -> Unit = { },
    copyCommand: () -> Unit = { },
) {
    AlertDialog(
        modifier = modifier.testTag(SetupAdbDialogTestTag),
        onDismissRequest = onDismissed,
        confirmButton = {
            TextButton(
                onClick = {
                    checkPermission()
                    onDismissed()
                },
            ) {
                Text(text = stringResource(id = Strings.check))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    copyCommand()
                    onDismissed()
                },
            ) {
                Text(text = stringResource(id = android.R.string.copy))
            }
        },
        icon = { Icon(painter = painterResource(id = Icons.ic_dialog_adb), contentDescription = null) },
        title = { Text(text = stringResource(id = Strings.adb)) },
        text = { Text(text = message) },
    )
}

const val SetupAdbButtonTestTag = "SetupAdbButton"
const val SetupAdbDialogTestTag = "SetupAdbDialog"

@DayNightPreview
@Composable
private fun SetupScreenContentPreview() {
    LogFoxTheme {
        SetupScreenContent()
    }
}

@DayNightPreview
@Composable
private fun SetupScreenContentWithDialogPreview() {
    LogFoxTheme {
        SetupScreenContent(
            state = SetupState(
                showAdbDialog = true,
                adbCommand = "HESOYAM",
            )
        )
    }
}
