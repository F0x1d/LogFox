package com.f0x1d.logfox.compose.designsystem.component.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    verticalPadding: Dp = DefaultVerticalPadding,
    horizontalPadding: Dp = DefaultHorizontalPadding,
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(modifier = modifier) {
        val searchBarVerticalPadding by animateDpAsState(
            targetValue = if (active) 0.dp else verticalPadding,
            label = "Search bar vertical padding",
        )
        val searchBarHorizontalPadding by animateDpAsState(
            targetValue = if (active) 0.dp else horizontalPadding,
            label = "Search bar horizontal padding",
        )

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = searchBarVerticalPadding,
                    horizontal = searchBarHorizontalPadding,
                ),
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            active = active,
            onActiveChange = onActiveChange,
            enabled = enabled,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            content = content,
        )
    }
}

private val DefaultVerticalPadding = 10.dp
private val DefaultHorizontalPadding = 15.dp
