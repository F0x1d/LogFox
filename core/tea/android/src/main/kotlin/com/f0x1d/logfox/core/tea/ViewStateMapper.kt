package com.f0x1d.logfox.core.tea

interface ViewStateMapper<State, ViewState> {
    fun map(state: State): ViewState
}
