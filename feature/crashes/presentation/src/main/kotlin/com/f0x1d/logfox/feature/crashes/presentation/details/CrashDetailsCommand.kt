package com.f0x1d.logfox.feature.crashes.presentation.details

import android.net.Uri
import com.f0x1d.logfox.feature.database.model.AppCrash

sealed interface CrashDetailsCommand {
    data class CrashLoaded(val crash: AppCrash, val crashLog: String?) : CrashDetailsCommand

    data class BlacklistStatusLoaded(val blacklisted: Boolean?) : CrashDetailsCommand

    data class PreferencesUpdated(
        val wrapCrashLogLines: Boolean,
        val useSeparateNotificationsChannelsForCrashes: Boolean,
    ) : CrashDetailsCommand

    data class ExportCrashToZip(val uri: Uri) : CrashDetailsCommand

    data class ChangeBlacklist(val appCrash: AppCrash) : CrashDetailsCommand

    data class DeleteCrash(val appCrash: AppCrash) : CrashDetailsCommand
}
