package com.f0x1d.logfox.core.preferences

import kotlinx.coroutines.flow.StateFlow

interface PreferenceStateFlow<T> : StateFlow<T> {
    fun set(value: T)
}
