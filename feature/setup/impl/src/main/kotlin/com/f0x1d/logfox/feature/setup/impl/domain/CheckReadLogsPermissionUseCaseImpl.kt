package com.f0x1d.logfox.feature.setup.impl.domain

import android.content.Context
import com.f0x1d.logfox.core.context.hasPermissionToReadLogs
import com.f0x1d.logfox.feature.setup.api.domain.CheckReadLogsPermissionUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class CheckReadLogsPermissionUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CheckReadLogsPermissionUseCase {

    override fun invoke(): Boolean = context.hasPermissionToReadLogs
}
