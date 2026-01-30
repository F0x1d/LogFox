package com.f0x1d.logfox.feature.crashes.presentation.appcrashes

import com.f0x1d.logfox.feature.crashes.api.model.AppCrashesCount
import com.f0x1d.logfox.feature.crashes.presentation.common.model.AppCrashesCountItem

sealed interface AppCrashesCommand {
    data class CrashesLoaded(val crashes: List<AppCrashesCount>) : AppCrashesCommand

    data class DeleteCrash(val item: AppCrashesCountItem) : AppCrashesCommand

    data class CrashClicked(val crashId: Long) : AppCrashesCommand
}
