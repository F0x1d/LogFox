package com.f0x1d.logfox.arch.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.model.event.Event
import com.f0x1d.logfox.model.event.NoDataEvent
import com.f0x1d.logfox.model.event.SnackbarEvent
import com.f0x1d.logfox.strings.Strings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {

    val ctx: Context get() = getApplication()

    val eventsData = MutableLiveData<Event>()
    val snackbarEventsData = MutableLiveData<SnackbarEvent>()

    fun launchCatching(
        context: CoroutineContext = Dispatchers.Main,
        errorBlock: suspend CoroutineScope.() -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context) {
        try {
            coroutineScope {
                block(this)
            }
        } catch (e: Exception) {
            if (e is CancellationException) return@launch

            errorBlock(this)

            e.printStackTrace()

            snackbar(ctx.getString(Strings.error, e.localizedMessage))
        }
    }

    protected fun snackbar(id: Int) = snackbar(ctx.getString(id))

    protected fun snackbar(text: String) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            snackbarEventsData.value = SnackbarEvent(text)
        }
    }

    protected fun sendEvent(type: String, data: Any) = eventsData.sendEvent(type, data)
    protected fun sendEvent(type: String) = eventsData.sendEvent(type)

    private fun MutableLiveData<Event>.sendEvent(type: String, data: Any) = postValue(Event(type, data))
    private fun MutableLiveData<Event>.sendEvent(type: String) = postValue(NoDataEvent(type))
}
