package com.f0x1d.logfox.core.tea

interface ViewStateMapper<State, ViewState> {
    fun map(state: State): ViewState

    companion object {
        fun <State> identity(): ViewStateMapper<State, State> = object : ViewStateMapper<State, State> {
            override fun map(state: State): State = state
        }
    }
}
