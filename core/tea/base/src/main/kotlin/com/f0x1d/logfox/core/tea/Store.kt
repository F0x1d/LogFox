package com.f0x1d.logfox.core.tea

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class Store<State, Command, SideEffect>(
    initialState: State,
    private val reducer: Reducer<State, Command, SideEffect>,
    private val effectHandlers: List<EffectHandler<SideEffect, Command>>,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _sideEffects = MutableSharedFlow<SideEffect>()
    val sideEffects: SharedFlow<SideEffect> = _sideEffects.asSharedFlow()

    private val jobs = mutableMapOf<String, Job>()

    fun send(command: Command) {
        val result = reducer.reduce(_state.value, command)
        _state.value = result.state

        result.sideEffects.forEach { sideEffect ->
            scope.launch {
                _sideEffects.emit(sideEffect)
            }

            effectHandlers.forEach { handler ->
                val jobId = UUID.randomUUID().toString()
                val job = scope.launch {
                    handler.handle(sideEffect) { cmd ->
                        withContext(Dispatchers.Main) {
                            send(cmd)
                        }
                    }
                    jobs.remove(jobId)
                }
                jobs[jobId] = job
            }
        }
    }

    fun cancel() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()
    }
}
