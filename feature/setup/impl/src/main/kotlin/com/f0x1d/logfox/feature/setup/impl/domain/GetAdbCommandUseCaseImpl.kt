package com.f0x1d.logfox.feature.setup.impl.domain

import android.Manifest
import android.content.Context
import com.f0x1d.logfox.feature.setup.api.domain.GetAdbCommandUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class GetAdbCommandUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : GetAdbCommandUseCase {

    override fun invoke(): String = "adb shell pm grant ${context.packageName} ${Manifest.permission.READ_LOGS}"
}
