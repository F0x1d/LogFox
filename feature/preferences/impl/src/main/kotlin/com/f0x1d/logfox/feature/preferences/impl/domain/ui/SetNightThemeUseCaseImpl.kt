package com.f0x1d.logfox.feature.preferences.impl.domain.ui

import com.f0x1d.logfox.feature.preferences.data.UISettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.ui.SetNightThemeUseCase
import javax.inject.Inject

internal class SetNightThemeUseCaseImpl @Inject constructor(
    private val uiSettingsRepository: UISettingsRepository,
) : SetNightThemeUseCase {

    override fun invoke(theme: Int) {
        uiSettingsRepository.nightTheme().set(theme)
    }
}
