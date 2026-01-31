package com.f0x1d.logfox.feature.logging.presentation.search

internal sealed interface SearchLogsCommand {
    data object Load : SearchLogsCommand

    data class QueryLoaded(val query: String?) : SearchLogsCommand

    data class CaseSensitiveLoaded(val caseSensitive: Boolean) : SearchLogsCommand

    data class UpdateQuery(val query: String?) : SearchLogsCommand

    data object ToggleCaseSensitive : SearchLogsCommand
}
