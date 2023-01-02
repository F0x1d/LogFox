package com.f0x1d.logfox.viewmodel.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.R
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.utils.event.SnackbarEvent
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {

    val ctx: Context get() = getApplication()

    val eventsData = MutableLiveData<Event>()
    val snackbarEventsData = MutableLiveData<SnackbarEvent>()

    protected fun launchCatching(
        context: CoroutineContext,
        errorBlock: suspend CoroutineScope.() -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context) {
        try {
            coroutineScope {
                block.invoke(this)
            }
        } catch (e: Exception) {
            if (e is CancellationException) return@launch

            errorBlock.invoke(this)

            e.printStackTrace()

            snackbar(ctx.getString(R.string.error, e.localizedMessage))
        }
    }

    protected fun snackbar(id: Int) = snackbar(ctx.getString(id))

    protected fun snackbar(text: String) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            snackbarEventsData.value = SnackbarEvent(text)
        }
    }
}