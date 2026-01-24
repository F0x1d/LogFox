package com.f0x1d.logfox.feature.setup.impl.domain

import android.Manifest
import android.content.Context
import com.f0x1d.logfox.feature.setup.api.domain.ExecuteGrantViaRootUseCase
import com.f0x1d.logfox.feature.terminals.base.Terminal
import com.f0x1d.logfox.feature.terminals.di.RootTerminal
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class ExecuteGrantViaRootUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @RootTerminal private val rootTerminal: Terminal,
) : ExecuteGrantViaRootUseCase {

    private val grantCommand: Array<String>
        get() = arrayOf("pm", "grant", context.packageName, Manifest.permission.READ_LOGS)

    override suspend fun invoke(): Boolean = if (rootTerminal.isSupported()) {
        rootTerminal.executeNow(*grantCommand)
        true
    } else {
        false
    }
}
