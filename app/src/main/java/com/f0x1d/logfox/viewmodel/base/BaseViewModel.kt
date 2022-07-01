package com.f0x1d.logfox.viewmodel.base

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.R
import com.f0x1d.logfox.utils.event.Event
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {

    val ctx: Context
        get() = getApplication()

    val eventsData = MutableLiveData<Event>()

    protected fun launchCatching(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(context) {
        try {
            coroutineScope {
                block.invoke(this)
            }
        } catch (e: Exception) {
            if (e is CancellationException) return@launch

            e.printStackTrace()

            withContext(Dispatchers.Main.immediate) {
                Toast.makeText(ctx, ctx.getString(R.string.error, e.localizedMessage), Toast.LENGTH_SHORT).show()
            }
        }
    }
}