package com.f0x1d.logfox.feature.preferences.impl.domain.datetime

import com.f0x1d.logfox.feature.preferences.api.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.SetDateFormatUseCase
import javax.inject.Inject

internal class SetDateFormatUseCaseImpl @Inject constructor(
    private val dateTimeSettingsRepository: DateTimeSettingsRepository,
) : SetDateFormatUseCase {

    override fun invoke(format: String) {
        dateTimeSettingsRepository.dateFormat().set(format)
    }
}
