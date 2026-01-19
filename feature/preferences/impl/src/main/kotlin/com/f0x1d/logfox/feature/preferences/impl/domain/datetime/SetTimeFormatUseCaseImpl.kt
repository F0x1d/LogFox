package com.f0x1d.logfox.feature.preferences.impl.domain.datetime

import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.datetime.SetTimeFormatUseCase
import javax.inject.Inject

internal class SetTimeFormatUseCaseImpl @Inject constructor(
    private val dateTimeSettingsRepository: DateTimeSettingsRepository,
) : SetTimeFormatUseCase {

    override fun invoke(format: String) {
        dateTimeSettingsRepository.timeFormat().set(format)
    }
}
