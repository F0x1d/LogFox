package com.f0x1d.logfox.core.preferences

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
interface PreferenceStateFlow<T> : StateFlow<T> {
    fun set(value: T)
}
