package com.f0x1d.logfox.feature.logging.search.presentation

sealed interface SearchLogsAction {
    data object Dismiss : SearchLogsAction
}
