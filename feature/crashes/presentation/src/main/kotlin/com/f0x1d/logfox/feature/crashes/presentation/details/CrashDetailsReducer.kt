package com.f0x1d.logfox.feature.crashes.presentation.details

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
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

        is CrashDetailsCommand.ChangeBlacklist -> state.withSideEffects(
            CrashDetailsSideEffect.ChangeBlacklist(command.appCrash),
        )

        is CrashDetailsCommand.DeleteCrash -> state.withSideEffects(
            CrashDetailsSideEffect.DeleteCrash(command.appCrash),
            CrashDetailsSideEffect.Close,
        )

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
    }

    private fun exportFilename(packageName: String, dateAndTime: Long, extension: String): String {
        val pkg = packageName.replace(".", "-")
        val formattedDate = dateTimeFormatter.formatForExport(dateAndTime)
        return "crash-$pkg-$formattedDate.$extension"
    }
}
