package com.f0x1d.logfox.arch.viewmodel

import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseStateViewModel<T>(
    initialStateProvider: () -> T,
    application: Application,
) : BaseViewModel(application) {

    private val mutableUiState = MutableStateFlow(initialStateProvider())
    val uiState = mutableUiState.asStateFlow()
    val currentState: T get() = uiState.value

    protected fun state(block: T.() -> T) = mutableUiState.update(block)
}
