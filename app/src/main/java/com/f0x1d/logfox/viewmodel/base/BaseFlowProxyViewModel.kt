package com.f0x1d.logfox.viewmodel.base

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class BaseFlowProxyViewModel<T, R>(application: Application, private val flow: Flow<T?>): BaseViewModel(application) {

    val data = MutableLiveData<R?>()
    val distinctiveData = data.distinctUntilChanged()

    protected var currentJob: Job? = null

    abstract fun map(data: T?): R?

    init {
        restartCollector()
    }

    protected fun restartCollector() {
        stopCollector()

        startCollector {
            data.postValue(it)
        }
    }

    protected fun collectOneValue() {
        stopCollector()

        startCollector {
            data.postValue(it)
            stopCollector()
        }
    }

    protected fun stopCollector() {
        currentJob?.cancel()
    }

    private fun startCollector(block: suspend (R?) -> Unit) {
        currentJob = viewModelScope.launch(Dispatchers.Default) {
            flow.map { map(it) }.collect {
                block.invoke(it)
            }
        }
    }
}