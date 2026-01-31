package com.f0x1d.logfox.feature.logging.presentation.search

internal data class SearchLogsState(
    val query: String?,
    val caseSensitive: Boolean,
)
