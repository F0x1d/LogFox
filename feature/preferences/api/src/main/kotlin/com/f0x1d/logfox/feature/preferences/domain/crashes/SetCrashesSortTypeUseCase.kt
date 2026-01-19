package com.f0x1d.logfox.feature.preferences.domain.crashes

import com.f0x1d.logfox.feature.preferences.CrashesSort

interface SetCrashesSortTypeUseCase {
    operator fun invoke(sortType: CrashesSort)
}
