package com.f0x1d.logfox.compose.designsystem.component.placeholder

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.compose.base.preview.DayNightPreview
import com.f0x1d.logfox.compose.designsystem.Icons
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.feature.strings.Strings

@Composable
fun ListPlaceholder(
    @DrawableRes iconResId: Int,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(26.dp),
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(iconResId),
                contentDescription = null,
            )
        }

        ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
            text()
        }
    }
}

@DayNightPreview
@Composable
private fun Preview() = LogFoxTheme {
    ListPlaceholder(
        iconResId = Icons.ic_recording,
        text = {
            Text(text = stringResource(Strings.no_crashes))
        },
    )
}
