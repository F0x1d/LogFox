package com.f0x1d.logfox.arch.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<S, A>(
    initialStateProvider: () -> S,
    application: Application,
) : AndroidViewModel(application) {
    val state: StateFlow<S> get() = mutableState.asStateFlow()
    val actions: Flow<A> get() = actionsChannel.receiveAsFlow()

    val currentState: S get() = mutableState.value

    protected val ctx: Context get() = getApplication()

    private val mutableState = MutableStateFlow(initialStateProvider())
    private val actionsChannel = Channel<A>(capacity = Channel.UNLIMITED)

    protected fun reduce(block: S.() -> S) = mutableState.update(block)

    protected fun sendAction(action: A) {
        actionsChannel.trySend(action)
    }

    protected fun launchCatching(
        context: CoroutineContext = Dispatchers.Main,
        errorBlock: suspend CoroutineScope.() -> Unit = { },
        block: suspend CoroutineScope.() -> Unit,
    ) = viewModelScope.launch(context) {
        try {
            coroutineScope {
                block(this)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            errorBlock(this)

            e.printStackTrace()
        }
    }
}
