package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.feature.crashes.api.model.AppCrashesCount

internal sealed interface AppCrashesCommand {
    data class CrashesLoaded(val crashes: List<AppCrashesCount>) : AppCrashesCommand

    data class DeleteCrash(val crashId: Long) : AppCrashesCommand

    data class CrashClicked(val crashId: Long) : AppCrashesCommand
}
