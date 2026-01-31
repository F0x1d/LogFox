package com.f0x1d.logfox.feature.preferences.impl.domain.ui

import com.f0x1d.logfox.feature.preferences.data.UISettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.ui.GetNightThemeUseCase
import javax.inject.Inject

internal class GetNightThemeUseCaseImpl @Inject constructor(
    private val uiSettingsRepository: UISettingsRepository,
) : GetNightThemeUseCase {

    override fun invoke(): Int = uiSettingsRepository.nightTheme().value
}
