package com.f0x1d.logfox.feature.recordings.presentation.list.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.f0x1d.logfox.compose.base.preview.DayNightPreview
import com.f0x1d.logfox.compose.designsystem.component.placeholder.ListPlaceholder
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.database.model.LogRecording
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.presentation.list.RecordingsState
import com.f0x1d.logfox.feature.recordings.presentation.list.ui.MockRecordingsScreenListener
import com.f0x1d.logfox.feature.recordings.presentation.list.ui.RecordingsScreenListener
import com.f0x1d.logfox.feature.strings.Strings
import java.io.File
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecordingsScreenContent(
    modifier: Modifier = Modifier,
    state: RecordingsState = RecordingsState(),
    listener: RecordingsScreenListener = MockRecordingsScreenListener,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topAppBarState)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopRecordingsBar(
                modifier = modifier,
                onClearClick = listener.onClearClick,
                onSaveAllClick = listener.onSaveAllClick,
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { contentPadding ->
        RecordingsItems(
            state = state,
            listener = listener,
            contentPadding = contentPadding,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopRecordingsBar(
    modifier: Modifier = Modifier,
    onClearClick: () -> Unit = { },
    onSaveAllClick: () -> Unit = { },
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text(text = stringResource(Strings.recordings)) },
        actions = {
            IconButton(onClick = onClearClick) {
                Icon(
                    painter = painterResource(Icons.ic_clear_all),
                    contentDescription = null,
                )
            }

            var showMenu by remember { mutableStateOf(false) }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    painter = painterResource(Icons.ic_menu_overflow),
                    contentDescription = null,
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(Strings.save_all_logs)) },
                    onClick = onSaveAllClick,
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun RecordingsItems(
    state: RecordingsState,
    listener: RecordingsScreenListener,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            RecordingControlsItem(
                recordingState = state.recordingState,
                onStartStopClick = listener.onStartStopClick,
                onPauseResumeClick = listener.onPauseResumeClick,
            )
        }

        if (state.recordings.isEmpty()) {
            item {
                ListPlaceholder(
                    modifier = Modifier
                        .animateItem(placementSpec = null)
                        .padding(vertical = 20.dp),
                    iconResId = Icons.ic_recording,
                    text = { Text(text = stringResource(Strings.no_recordings)) },
                )
            }
        }

        itemsIndexed(
            items = state.recordings,
            key = { _, item -> item.id },
        ) { index, item ->
            RecordingItem(
                modifier = Modifier.animateItem(),
                logRecording = item,
                onRecordingClick = listener.onRecordingClick,
                onRecordingDeleteClick = listener.onRecordingDeleteClick,
            )

            if (index != state.recordings.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        start = 80.dp,
                        end = 10.dp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun RecordingItem(
    logRecording: LogRecording,
    modifier: Modifier = Modifier,
    onRecordingClick: (LogRecording) -> Unit = { },
    onRecordingDeleteClick: (LogRecording) -> Unit = { },
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onRecordingClick(logRecording) }
            .padding(
                vertical = 10.dp,
                horizontal = 10.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            modifier = Modifier
                .size(60.dp)
                .padding(15.dp),
            painter = painterResource(Icons.ic_recording),
            contentDescription = null,
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = logRecording.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            val dateText = remember(logRecording.dateAndTime) {
                Date(logRecording.dateAndTime).toLocaleString()
            }
            Text(
                text = dateText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        IconButton(
            onClick = { onRecordingDeleteClick(logRecording) },
        ) {
            Icon(
                painter = painterResource(Icons.ic_delete),
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null,
            )
        }
    }
}

internal val MockRecordingsState = RecordingsState(
    recordings = listOf(
        LogRecording(
            title = "Cool",
            dateAndTime = 0L,
            file = File(""),
        ),
        LogRecording(
            title = "Cool",
            dateAndTime = 0L,
            file = File(""),
            id = 1,
        ),
    ),
    recordingState = RecordingState.RECORDING,
)

@DayNightPreview
@Composable
private fun ScreenContentPreview() = LogFoxTheme {
    RecordingsScreenContent(
        state = MockRecordingsState,
    )
}
