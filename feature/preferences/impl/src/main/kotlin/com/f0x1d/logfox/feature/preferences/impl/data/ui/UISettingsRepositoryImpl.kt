package com.f0x1d.logfox.feature.preferences.impl.data.ui

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow
import com.f0x1d.logfox.core.preferences.asPreferenceStateFlow
import com.f0x1d.logfox.feature.preferences.data.UISettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UISettingsRepositoryImpl @Inject constructor(
    private val localDataSource: UISettingsLocalDataSource,
) : UISettingsRepository {

    override fun nightTheme(): PreferenceStateFlow<Int> = localDataSource.nightTheme().asPreferenceStateFlow()

    override fun monetEnabled(): PreferenceStateFlow<Boolean> = localDataSource.monetEnabled().asPreferenceStateFlow()
}
