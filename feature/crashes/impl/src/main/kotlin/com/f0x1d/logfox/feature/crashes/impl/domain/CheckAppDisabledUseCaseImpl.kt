package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.DisabledAppsRepository
import com.f0x1d.logfox.feature.crashes.api.domain.CheckAppDisabledUseCase
import javax.inject.Inject

internal class CheckAppDisabledUseCaseImpl @Inject constructor(
    private val disabledAppsRepository: DisabledAppsRepository,
) : CheckAppDisabledUseCase {
    override suspend fun invoke(packageName: String) =
        disabledAppsRepository.checkApp(packageName)

    override suspend fun invoke(packageName: String, checked: Boolean) =
        disabledAppsRepository.checkApp(packageName, checked)
}
