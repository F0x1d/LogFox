package com.f0x1d.logfox.extensions

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> MutableLiveData<T>.suspendAndPostValue(data: T) = withContext(Dispatchers.Main.immediate) {
    value = data
}