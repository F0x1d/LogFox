package com.f0x1d.logfox.feature.logging.presentation.service

import com.f0x1d.logfox.core.tea.Store
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

internal class LoggingServiceStoreFactory @Inject constructor(
    private val reducer: LoggingServiceReducer,
    private val effectHandler: LoggingServiceEffectHandler,
) {
    fun create(
        scope: CoroutineScope,
    ): Store<LoggingServiceState, LoggingServiceCommand, LoggingServiceSideEffect> = Store(
        initialState = LoggingServiceState(),
        reducer = reducer,
        effectHandlers = listOf(effectHandler),
        scope = scope,
    )
}
