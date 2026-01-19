package com.f0x1d.logfox.feature.preferences.impl.data.ui

import com.f0x1d.logfox.feature.preferences.data.UISettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UISettingsRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: UISettingsLocalDataSource,
    ) : UISettingsRepository {
        override var nightTheme: Int
            get() = localDataSource.nightTheme
            set(value) {
                localDataSource.nightTheme = value
            }

        override val nightThemeFlow: Flow<Int>
            get() = localDataSource.nightThemeFlow

        override var monetEnabled: Boolean
            get() = localDataSource.monetEnabled
            set(value) {
                localDataSource.monetEnabled = value
            }
    }
