package com.f0x1d.logfox.core.tea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseStoreViewModel<ViewState, State, Command, SideEffect>(
    initialState: State,
    reducer: Reducer<State, Command, SideEffect>,
    effectHandlers: List<EffectHandler<SideEffect, Command>>,
    viewStateMapper: ViewStateMapper<State, ViewState>,
    initialSideEffects: List<SideEffect> = emptyList(),
    viewStateMappingDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {

    private val store = Store(
        initialState = initialState,
        reducer = reducer,
        effectHandlers = effectHandlers,
        scope = viewModelScope,
    )

    val state: StateFlow<ViewState> = store.state
        .map { viewStateMapper.map(it) }
        .flowOn(viewStateMappingDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewStateMapper.map(initialState),
        )
    val sideEffects: Flow<SideEffect> = store.sideEffects

    init {
        initialSideEffects.forEach { effect ->
            effectHandlers.forEach { handler ->
                viewModelScope.launch {
                    handler.handle(effect) { cmd ->
                        withContext(Dispatchers.Main.immediate) {
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
