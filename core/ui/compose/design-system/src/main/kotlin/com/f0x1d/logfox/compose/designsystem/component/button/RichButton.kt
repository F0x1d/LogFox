package com.f0x1d.logfox.compose.designsystem.component.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.compose.base.preview.DayNightPreview
import com.f0x1d.logfox.compose.designsystem.Icons
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme

@Composable
fun RichButton(
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
    ) {
        Box(modifier = Modifier.size(24.dp)) {
            icon()
        }
        Spacer(modifier = Modifier.width(8.dp))
        ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
            text()
        }
    }
}

@DayNightPreview
@Composable
private fun RichButtonAdbPreview() = LogFoxTheme {
    RichButton(
        text = { Text(text = "ADB") },
        icon = {
            Icon(
                painter = painterResource(id = Icons.ic_adb),
                contentDescription = null,
            )
        },
        onClick = { },
    )
}
