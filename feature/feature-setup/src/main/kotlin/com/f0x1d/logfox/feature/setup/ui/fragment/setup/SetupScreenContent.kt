package com.f0x1d.logfox.feature.setup.ui.fragment.setup

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.feature.setup.viewmodel.SetupViewModel
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import com.f0x1d.logfox.ui.compose.component.button.RichButton
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme

@Composable
internal fun SetupScreenContent(viewModel: SetupViewModel) {
    ScreenContent(
        onRootClick = viewModel::root,
        onAdbClick = viewModel::adb,
        onShizukuClick = viewModel::shizuku,
    )

    if (viewModel.showAdbDialog) {
        AdbDialog(
            message = stringResource(
                id = Strings.how_to_use_adb,
                remember { viewModel.adbCommand },
            ),
            onDismissed = { viewModel.showAdbDialog = false },
            checkPermission = viewModel::checkPermission,
            copyCommand = viewModel::copyCommand,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenContent(
    onRootClick: () -> Unit = { },
    onAdbClick: () -> Unit = { },
    onShizukuClick: () -> Unit = { },
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
                    text = Strings.root,
                    icon = Icons.ic_square_root,
                    onClick = onRootClick,
                )

                RichButton(
                    text = Strings.adb,
                    icon = Icons.ic_adb,
                    onClick = onAdbClick,
                )

                RichButton(
                    text = Strings.shizuku,
                    icon = Icons.ic_terminal,
                    onClick = onShizukuClick,
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
        modifier = modifier,
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

@Preview
@Composable
private fun ScreenContentPreview() {
    LogFoxTheme {
        ScreenContent()
    }
}

@Preview
@Composable
private fun AdbDialogPreview() {
    LogFoxTheme {
        AdbDialog(
            message = "ADB is cool!"
        )
    }
}
