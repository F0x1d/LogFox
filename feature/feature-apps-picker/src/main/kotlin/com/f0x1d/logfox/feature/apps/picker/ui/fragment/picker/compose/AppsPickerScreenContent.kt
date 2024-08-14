package com.f0x1d.logfox.feature.apps.picker.ui.fragment.picker.compose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.compose.component.button.NavigationBackButton
import com.f0x1d.logfox.ui.compose.preview.DayNightPreview
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun AppsPickerScreenContent(
    state: AppsPickerScreenState = AppsPickerScreenState(),
    listener: AppsPickerScreenListener = MockAppsPickerScreenListener,
) {
    Scaffold(
        topBar = {
            AppsSearchBar(
                state = state,
                listener = listener,
            )
        },
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingContent(modifier = Modifier.padding(paddingValues))
        } else {
            AppsContent(
                items = state.apps,
                listener = listener,
                contentPadding = paddingValues,
            )
        }
    }

    BackHandler(
        enabled = state.searchActive,
        onBack = listener.onBackClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppsSearchBar(
    state: AppsPickerScreenState,
    listener: AppsPickerScreenListener,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        val searchBarPadding by animateDpAsState(
            targetValue = if (state.searchActive) 0.dp else 10.dp,
            label = "Search bar padding",
        )

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(searchBarPadding),
            query = state.query,
            onQueryChange = listener.onQueryChanged,
            onSearch = { /* noop */ },
            active = state.searchActive,
            onActiveChange = listener.onSearchActiveChanged,
            placeholder = { Text(text = stringResource(id = Strings.apps)) },
            leadingIcon = { NavigationBackButton(onClick = listener.onBackClicked) }
        ) {
            AppsContent(
                items = state.searchedApps,
                listener = listener,
                contentPadding = WindowInsets.navigationBars
                    .union(WindowInsets.ime)
                    .asPaddingValues(),
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AppsContent(
    items: ImmutableList<InstalledApp>,
    listener: AppsPickerScreenListener,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id },
            contentType = { _, item -> item.javaClass },
        ) { index, item ->
            Column {
                AppContent(
                    item = item,
                    onClick = listener.onAppClicked,
                )
                
                if (index != items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            start = 80.dp,
                            end = 10.dp,
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AppContent(
    item: InstalledApp,
    onClick: (InstalledApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable { onClick(item) }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AsyncImage(
            modifier = Modifier.size(60.dp),
            model = item,
            contentDescription = null,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = item.packageName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

internal val MockAppsPickerScreenState = AppsPickerScreenState(
    apps = persistentListOf(
        InstalledApp("LogFox", "com.f0x1d.logfox"),
        InstalledApp("Sense", "com.f0x1d.sense")
    ),
    isLoading = false,
)

@DayNightPreview
@Composable
private fun AppsPickerScreenContentPreview() = LogFoxTheme {
    AppsPickerScreenContent(
        state = MockAppsPickerScreenState,
    )
}

@DayNightPreview
@Composable
private fun AppContentPreview() = LogFoxTheme {
    AppContent(
        item = MockAppsPickerScreenState.apps.first(),
        onClick = { },
    )
}
