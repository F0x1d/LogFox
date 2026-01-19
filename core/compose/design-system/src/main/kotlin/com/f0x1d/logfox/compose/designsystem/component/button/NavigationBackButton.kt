package com.f0x1d.logfox.compose.designsystem.component.button

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.f0x1d.logfox.compose.base.preview.DayNightPreview
import com.f0x1d.logfox.compose.designsystem.Icons
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme

@Composable
fun NavigationBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(id = Icons.ic_arrow_back),
            contentDescription = null,
        )
    }
}

@DayNightPreview
@Composable
private fun Preview() = LogFoxTheme {
    NavigationBackButton(onClick = { })
}
