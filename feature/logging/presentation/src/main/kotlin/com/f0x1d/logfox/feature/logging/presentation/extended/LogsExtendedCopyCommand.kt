package com.f0x1d.logfox.feature.logging.presentation.extended

internal sealed interface LogsExtendedCopyCommand {
    data object Load : LogsExtendedCopyCommand

    data class TextLoaded(val text: String) : LogsExtendedCopyCommand
}
