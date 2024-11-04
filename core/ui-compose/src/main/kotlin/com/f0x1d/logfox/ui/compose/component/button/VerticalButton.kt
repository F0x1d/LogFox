package com.f0x1d.logfox.ui.compose.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.Icons
import com.f0x1d.logfox.ui.compose.preview.DayNightPreview
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme

@Composable
fun VerticalButton(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaults.shape,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = shape,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            icon()

            ProvideTextStyle(
                value = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                ),
            ) {
                text()
            }
        }
    }
}

@DayNightPreview
@Composable
private fun Preview() = LogFoxTheme {
    VerticalButton(
        icon = {
            Icon(
                painter = painterResource(Icons.ic_menu_overflow),
                contentDescription = null,
            )
        },
        text = {
            Text(text = stringResource(Strings.root))
        },
        onClick = { },
    )
}
