package com.f0x1d.logfox.arch.ui.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

interface SimpleLifecycleOwner {

    val lifecycle: Lifecycle
    fun getViewLifecycleOwner(): LifecycleOwner? = null

    fun <T> Flow<T>.collectWithLifecycle(
        state: Lifecycle.State = Lifecycle.State.STARTED,
        collector: FlowCollector<T>,
    ) {
        val lifecycle = getViewLifecycleOwner()?.lifecycle ?: lifecycle

        lifecycle.coroutineScope.launch {
            lifecycle.repeatOnLifecycle(state) {
                collect(collector)
            }
        }
    }
}

interface SimpleFragmentLifecycleOwner : SimpleLifecycleOwner {
    override fun getViewLifecycleOwner(): LifecycleOwner
}
