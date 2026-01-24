package com.f0x1d.logfox.feature.filters.api.domain

import com.f0x1d.logfox.feature.filters.api.model.UserFilter

interface SwitchFilterUseCase {
    suspend operator fun invoke(userFilter: UserFilter, checked: Boolean)
}
