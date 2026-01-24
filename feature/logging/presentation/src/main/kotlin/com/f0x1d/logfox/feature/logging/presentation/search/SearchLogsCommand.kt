package com.f0x1d.logfox.feature.logging.presentation.search

sealed interface SearchLogsCommand {
    data object Load : SearchLogsCommand

    data class QueryLoaded(val query: String?) : SearchLogsCommand

    data class UpdateQuery(val query: String?) : SearchLogsCommand
}
