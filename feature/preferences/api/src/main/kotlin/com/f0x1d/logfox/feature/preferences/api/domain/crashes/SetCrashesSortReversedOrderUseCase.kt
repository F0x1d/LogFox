package com.f0x1d.logfox.feature.preferences.api.domain.crashes

interface SetCrashesSortReversedOrderUseCase {
    operator fun invoke(reversed: Boolean)
}
