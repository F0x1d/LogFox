package com.f0x1d.logfox.feature.crashes.presentation.details

import android.net.Uri
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash

internal sealed interface CrashDetailsCommand {
    data class CrashLoaded(val crash: AppCrash, val crashLog: String?) : CrashDetailsCommand

    data class BlacklistStatusLoaded(val blacklisted: Boolean?) : CrashDetailsCommand

    data class PreferencesUpdated(
        val wrapCrashLogLines: Boolean,
        val useSeparateNotificationsChannelsForCrashes: Boolean,
    ) : CrashDetailsCommand

    data object WrapLinesClicked : CrashDetailsCommand

    data object OpenAppInfoClicked : CrashDetailsCommand

    data object OpenNotificationSettingsClicked : CrashDetailsCommand

    data object BlacklistClicked : CrashDetailsCommand

    data object ConfirmBlacklist : CrashDetailsCommand

    data object DeleteClicked : CrashDetailsCommand

    data object ConfirmDelete : CrashDetailsCommand

    data object ExportCrashToFileClicked : CrashDetailsCommand

    data object ExportCrashToZipClicked : CrashDetailsCommand

    data class ExportCrashToFile(val uri: Uri) : CrashDetailsCommand

    data class ExportCrashToZip(val uri: Uri) : CrashDetailsCommand

    data object CopyCrashLog : CrashDetailsCommand

    data object ShareCrashLog : CrashDetailsCommand

    data class SearchInLog(val query: String) : CrashDetailsCommand
}
