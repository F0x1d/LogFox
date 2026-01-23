package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.feature.database.model.AppCrash
import com.f0x1d.logfox.feature.database.model.AppCrashesCount

sealed interface AppCrashesCommand {
    data class CrashesLoaded(val crashes: List<AppCrashesCount>) : AppCrashesCommand

    data class DeleteCrash(val appCrash: AppCrash) : AppCrashesCommand

    data class CrashClicked(val crashId: Long) : AppCrashesCommand
}
