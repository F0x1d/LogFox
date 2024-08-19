package com.f0x1d.logfox.arch.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.strings.Strings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val eventsChannel = Channel<Event>(capacity = Channel.BUFFERED)
    val eventsFlow = eventsChannel.receiveAsFlow()

    protected val ctx: Context get() = getApplication()

    protected fun launchCatching(
        context: CoroutineContext = Dispatchers.Main,
        errorBlock: suspend CoroutineScope.() -> Unit = {},
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

            snackbar(ctx.getString(Strings.error, e.localizedMessage))
        }
    }

    protected fun snackbar(id: Int) = snackbar(ctx.getString(id))
    protected fun snackbar(text: String) = sendEvent(ShowSnackbar(text))

    protected fun sendEvent(event: Event) {
        viewModelScope.launch {
            eventsChannel.send(event)
        }
    }
}
