package com.f0x1d.logfox.feature.preferences.api.domain.crashes

import com.f0x1d.logfox.feature.preferences.api.CrashesSort

interface SetCrashesSortTypeUseCase {
    operator fun invoke(sortType: CrashesSort)
}
