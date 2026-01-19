package com.f0x1d.logfox.core.preferences

import com.fredporciuncula.flow.preferences.Preference
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.map

internal class MappedPreferenceStateFlowImpl<T : Any, R : Any>(
    private val preference: Preference<T>,
    private val mapGet: (T) -> R,
    private val mapSet: (R) -> T,
) : PreferenceStateFlow<R> {

    override val value: R get() = mapGet(preference.get())

    override val replayCache: List<R> get() = listOf(value)

    @Suppress("UNCHECKED_CAST")
    override suspend fun collect(collector: FlowCollector<R>): Nothing = preference.asFlow().map(mapGet).collect(collector) as Nothing

    override fun set(value: R) {
        preference.set(mapSet(value))
    }
}

fun <T : Any, R : Any> Preference<T>.asMappedPreferenceStateFlow(
    mapGet: (T) -> R,
    mapSet: (R) -> T,
): PreferenceStateFlow<R> = MappedPreferenceStateFlowImpl(this, mapGet, mapSet)
