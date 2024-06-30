package com.f0x1d.logfox.ui.compose.component.button

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.compose.R
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme

@Composable
fun RichButton(
    @StringRes text: Int,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview
@Composable
private fun RichButtonPreview() {
    LogFoxTheme {
        RichButton(
            text = Strings.root,
            icon = R.drawable.ic_test_icon,
            onClick = { },
        )
    }
}
