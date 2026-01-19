package com.f0x1d.logfox.feature.crashes.presentation.details

import com.f0x1d.logfox.core.tea.ReduceResult
import com.f0x1d.logfox.core.tea.Reducer
import com.f0x1d.logfox.core.tea.noSideEffects
import com.f0x1d.logfox.core.tea.withSideEffects
import javax.inject.Inject

internal class CrashDetailsReducer @Inject constructor() : Reducer<CrashDetailsState, CrashDetailsCommand, CrashDetailsSideEffect> {

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
        )
    }
}
