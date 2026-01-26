package com.f0x1d.logfox.feature.logging.presentation.search

data class SearchLogsState(
    val query: String? = null,
    val caseSensitive: Boolean = false,
)
