package com.f0x1d.logfox.feature.preferences.impl.data.terminal

import com.f0x1d.logfox.feature.preferences.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TerminalSettingsRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: TerminalSettingsLocalDataSource,
    ) : TerminalSettingsRepository {
        override var selectedTerminalType: TerminalType
            get() = localDataSource.selectedTerminalType
            set(value) {
                localDataSource.selectedTerminalType = value
            }

        override val selectedTerminalTypeFlow: Flow<TerminalType>
            get() = localDataSource.selectedTerminalTypeFlow

        override var fallbackToDefaultTerminal: Boolean
            get() = localDataSource.fallbackToDefaultTerminal
            set(value) {
                localDataSource.fallbackToDefaultTerminal = value
            }

        override fun selectTerminal(type: TerminalType) {
            localDataSource.selectTerminal(type)
        }
    }
