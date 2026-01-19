package com.f0x1d.logfox.feature.logging.presentation.extended

sealed interface LogsExtendedCopyCommand {
    data object Load : LogsExtendedCopyCommand

    data class TextLoaded(val text: String) : LogsExtendedCopyCommand
}
