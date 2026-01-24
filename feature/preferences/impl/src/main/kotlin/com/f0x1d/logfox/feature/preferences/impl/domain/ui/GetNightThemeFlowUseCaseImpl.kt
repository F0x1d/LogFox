package com.f0x1d.logfox.feature.preferences.impl.domain.ui

import com.f0x1d.logfox.feature.preferences.data.UISettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.ui.GetNightThemeFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetNightThemeFlowUseCaseImpl @Inject constructor(
    private val uiSettingsRepository: UISettingsRepository,
) : GetNightThemeFlowUseCase {

    override fun invoke(): Flow<Int> = uiSettingsRepository.nightTheme()
}
