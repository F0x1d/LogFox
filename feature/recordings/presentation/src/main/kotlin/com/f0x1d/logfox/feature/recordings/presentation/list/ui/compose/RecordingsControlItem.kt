package com.f0x1d.logfox.feature.recordings.presentation.list.ui.compose

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.strings.Strings
import com.f0x1d.logfox.core.presentation.Icons
import com.f0x1d.logfox.compose.base.preview.DayNightPreview
import com.f0x1d.logfox.compose.designsystem.component.button.VerticalButton
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme

@Composable
internal fun RecordingControlsItem(
    recordingState: RecordingState,
    modifier: Modifier = Modifier,
    onStartStopClick: () -> Unit = { },
    onPauseResumeClick: () -> Unit = { },
) {
    val pauseVisible = recordingState == RecordingState.RECORDING || recordingState == RecordingState.PAUSED
    val pauseShownFraction by animateFloatAsState(
        targetValue = if (pauseVisible) {
            1f
        } else {
            0f
        },
        label = "pause shown fraction animation",
    )
    val gapWidthDp by animateDpAsState(
        targetValue = if (pauseVisible) {
            20.dp
        } else {
            0.dp
        },
        label = "gap width animation",
    )

    Layout(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 10.dp,
                horizontal = 20.dp,
            ),
        content = {
            val (startStopIconResId, startStopTextResId) = remember(recordingState) {
                when (recordingState) {
                    RecordingState.IDLE,
                    RecordingState.SAVING,
                        -> Icons.ic_recording to Strings.record

                    RecordingState.RECORDING,
                    RecordingState.PAUSED,
                        -> Icons.ic_stop to Strings.stop
                }
            }

            VerticalButton(
                modifier = Modifier.layoutId(RecordingsControlItem.RECORD),
                icon = {
                    Icon(
                        painter = painterResource(startStopIconResId),
                        contentDescription = null,
                    )
                },
                text = {
                    Text(text = stringResource(startStopTextResId))
                },
                onClick = onStartStopClick,
                enabled = recordingState != RecordingState.SAVING,
            )

            val (pauseResumeIconResId, pauseResumeTextResId) = remember(recordingState) {
                when (recordingState) {
                    RecordingState.IDLE,
                    RecordingState.SAVING,
                    RecordingState.RECORDING,
                        -> Icons.ic_pause to Strings.pause

                    RecordingState.PAUSED,
                        -> Icons.ic_play to Strings.resume
                }
            }

            VerticalButton(
                modifier = Modifier
                    .graphicsLayer { alpha = pauseShownFraction }
                    .layoutId(RecordingsControlItem.PAUSE),
                icon = {
                    Icon(
                        painter = painterResource(pauseResumeIconResId),
                        contentDescription = null,
                    )
                },
                text = {
                    Text(text = stringResource(pauseResumeTextResId))
                },
                onClick = onPauseResumeClick,
            )
        },
    ) { measurables, constraints ->
        val gapWidth = gapWidthDp.toPx()
        val halfGapWidth = gapWidth / 2f

        val maxWidth = constraints.maxWidth
        val halfWidth = maxWidth / 2f
        val pauseWidth = (halfWidth - halfGapWidth).toInt()
        //val recordWidth = (maxWidth - (pauseWidth + halfGapWidth) * pauseShownFraction).toInt()

        val recordWidth = lerp(
            start = maxWidth,
            stop = pauseWidth,
            fraction = pauseShownFraction,
        )

        val recordPlaceable = measurables
            .first { it.layoutId == RecordingsControlItem.RECORD }
            .measure(
                constraints = constraints.copy(
                    maxWidth = recordWidth,
                    minWidth = recordWidth,
                ),
            )

        val pausePlaceable = if (pauseShownFraction > 0) {
            measurables
                .first { it.layoutId == RecordingsControlItem.PAUSE }
                .measure(
                    constraints = constraints.copy(
                        maxWidth = pauseWidth,
                        minWidth = pauseWidth,
                    ),
                )
        } else {
            null
        }

        layout(maxWidth, recordPlaceable.height) {
            recordPlaceable.placeRelative(x = 0, y = 0)

            pausePlaceable?.placeRelative(
                x = lerp(
                    start = maxWidth,
                    stop = (recordWidth + gapWidth).toInt(),
                    fraction = pauseShownFraction,
                ),
                y = 0,
            )
        }
    }
}

private enum class RecordingsControlItem {
    RECORD,
    PAUSE,
}

@DayNightPreview
@Composable
private fun IdlePreview() = LogFoxTheme {
    RecordingControlsItem(recordingState = RecordingState.IDLE)
}

@DayNightPreview
@Composable
private fun RecordingPreview() = LogFoxTheme {
    RecordingControlsItem(recordingState = RecordingState.RECORDING)
}
