package com.f0x1d.logfox.core.tea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseStoreViewModel<State, Command, SideEffect>(
    initialState: State,
    reducer: Reducer<State, Command, SideEffect>,
    effectHandlers: List<EffectHandler<SideEffect, Command>>,
    initialSideEffects: List<SideEffect> = emptyList(),
) : ViewModel() {

    private val store = Store(
        initialState = initialState,
        reducer = reducer,
        effectHandlers = effectHandlers,
        scope = viewModelScope,
    )

    val state: StateFlow<State> = store.state
    val sideEffects: SharedFlow<SideEffect> = store.sideEffects

    init {
        initialSideEffects.forEach { effect ->
            effectHandlers.forEach { handler ->
                viewModelScope.launch {
                    handler.handle(effect) { cmd ->
                        withContext(Dispatchers.Main) {
                            send(cmd)
                        }
                    }
                }
            }
        }
    }

    fun send(command: Command) {
        store.send(command)
    }

    override fun onCleared() {
        super.onCleared()
        store.cancel()
    }
}
