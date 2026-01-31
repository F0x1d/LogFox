package com.f0x1d.logfox.feature.crashes.presentation.details

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import com.f0x1d.logfox.feature.crashes.api.data.notificationChannelId
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import javax.inject.Inject

internal class CrashDetailsReducer @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) : Reducer<CrashDetailsState, CrashDetailsCommand, CrashDetailsSideEffect> {

    override fun reduce(
        state: CrashDetailsState,
        command: CrashDetailsCommand,
    ): ReduceResult<CrashDetailsState, CrashDetailsSideEffect> = when (command) {
        is CrashDetailsCommand.CrashLoaded -> state.copy(
            crash = command.crash,
            crashLog = command.crashLog,
        ).noSideEffects()

        is CrashDetailsCommand.BlacklistStatusLoaded -> state.copy(
            blacklisted = command.blacklisted,
        ).noSideEffects()

        is CrashDetailsCommand.PreferencesUpdated -> state.copy(
            wrapCrashLogLines = command.wrapCrashLogLines,
            useSeparateNotificationsChannelsForCrashes = command.useSeparateNotificationsChannelsForCrashes,
        ).noSideEffects()

        is CrashDetailsCommand.OpenAppInfoClicked -> {
            val appCrash = state.crash
            if (appCrash != null) {
                state.withSideEffects(
                    CrashDetailsSideEffect.OpenAppInfo(appCrash.packageName),
                )
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.OpenNotificationSettingsClicked -> {
            val appCrash = state.crash
            if (appCrash != null) {
                state.withSideEffects(
                    CrashDetailsSideEffect.OpenNotificationSettings(appCrash.notificationChannelId),
                )
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.BlacklistClicked -> {
            val appCrash = state.crash
            if (appCrash != null) {
                if (state.blacklisted == false) {
                    state.withSideEffects(CrashDetailsSideEffect.ConfirmBlacklist)
                } else {
                    state.withSideEffects(CrashDetailsSideEffect.ChangeBlacklist(appCrash))
                }
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.ConfirmBlacklist -> {
            val appCrash = state.crash
            if (appCrash != null) {
                state.withSideEffects(CrashDetailsSideEffect.ChangeBlacklist(appCrash))
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.DeleteClicked -> state.withSideEffects(
            CrashDetailsSideEffect.ConfirmDelete,
        )

        is CrashDetailsCommand.ConfirmDelete -> {
            val appCrash = state.crash
            if (appCrash != null) {
                state.withSideEffects(
                    CrashDetailsSideEffect.DeleteCrash(appCrash),
                    CrashDetailsSideEffect.Close,
                )
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.ExportCrashToFileClicked -> {
            val appCrash = state.crash
            if (appCrash != null && state.crashLog != null) {
                state.withSideEffects(
                    CrashDetailsSideEffect.LaunchFileExportPicker(
                        filename = exportFilename(appCrash.packageName, appCrash.dateAndTime, "log"),
                    ),
                )
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.ExportCrashToZipClicked -> {
            val appCrash = state.crash
            if (appCrash != null) {
                state.withSideEffects(
                    CrashDetailsSideEffect.LaunchZipExportPicker(
                        filename = exportFilename(appCrash.packageName, appCrash.dateAndTime, "zip"),
                    ),
                )
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.ExportCrashToFile -> {
            val crashLog = state.crashLog
            if (crashLog != null) {
                state.withSideEffects(
                    CrashDetailsSideEffect.ExportCrashToFile(
                        uri = command.uri,
                        crashLog = crashLog,
                    ),
                )
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.ExportCrashToZip -> {
            val appCrash = state.crash
            if (appCrash != null) {
                state.withSideEffects(
                    CrashDetailsSideEffect.ExportCrashToZip(
                        uri = command.uri,
                        appCrash = appCrash,
                        crashLog = state.crashLog,
                    ),
                )
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.CopyCrashLog -> {
            val crashLog = state.crashLog
            if (crashLog != null) {
                state.withSideEffects(
                    CrashDetailsSideEffect.CopyText(crashLog),
                )
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.ShareCrashLog -> {
            val crashLog = state.crashLog
            if (crashLog != null) {
                state.withSideEffects(
                    CrashDetailsSideEffect.ShareCrashLog(crashLog),
                )
            } else {
                state.noSideEffects()
            }
        }

        is CrashDetailsCommand.SearchInLog -> {
            val query = command.query
            val crashLog = state.crashLog.orEmpty()
            val ranges = if (query.isNotEmpty()) {
                val lowerLog = crashLog.lowercase(java.util.Locale.ENGLISH)
                val lowerQuery = query.lowercase(java.util.Locale.ENGLISH)
                buildList {
                    var index = 0
                    while (true) {
                        index = lowerLog.indexOf(lowerQuery, index)
                        if (index == -1) break
                        add(index until index + lowerQuery.length)
                        index += lowerQuery.length
                    }
                }
            } else {
                emptyList()
            }
            state.copy(searchQuery = query, searchMatchRanges = ranges).noSideEffects()
        }
    }

    private fun exportFilename(packageName: String, dateAndTime: Long, extension: String): String {
        val pkg = packageName.replace(".", "-")
        val formattedDate = dateTimeFormatter.formatForExport(dateAndTime)
        return "crash-$pkg-$formattedDate.$extension"
    }
}
