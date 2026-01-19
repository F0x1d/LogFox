package com.f0x1d.logfox.core.preferences

import com.fredporciuncula.flow.preferences.Preference
import kotlinx.coroutines.flow.FlowCollector

internal class PreferenceStateFlowImpl<T : Any>(private val preference: Preference<T>) : PreferenceStateFlow<T> {

    override val value: T get() = preference.get()

    override val replayCache: List<T> get() = listOf(value)

    @Suppress("UNCHECKED_CAST")
    override suspend fun collect(collector: FlowCollector<T>): Nothing = preference.asFlow().collect(collector) as Nothing

    override fun set(value: T) {
        preference.set(value)
    }
}

fun <T : Any> Preference<T>.asPreferenceStateFlow(): PreferenceStateFlow<T> = PreferenceStateFlowImpl(this)
