package com.f0x1d.logfox.feature.preferences.impl.data.terminal

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow
import com.f0x1d.logfox.core.preferences.asMappedPreferenceStateFlow
import com.f0x1d.logfox.core.preferences.asPreferenceStateFlow
import com.f0x1d.logfox.feature.preferences.data.TerminalSettingsRepository
import com.f0x1d.logfox.feature.terminals.base.TerminalType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TerminalSettingsRepositoryImpl @Inject constructor(
    private val localDataSource: TerminalSettingsLocalDataSource,
) : TerminalSettingsRepository {

    override fun selectedTerminalType(): PreferenceStateFlow<TerminalType> = localDataSource.selectedTerminalTypeKey().asMappedPreferenceStateFlow(
        mapGet = TerminalType::fromKey,
        mapSet = TerminalType::key,
    )

    override fun fallbackToDefaultTerminal(): PreferenceStateFlow<Boolean> = localDataSource.fallbackToDefaultTerminal().asPreferenceStateFlow()
}
