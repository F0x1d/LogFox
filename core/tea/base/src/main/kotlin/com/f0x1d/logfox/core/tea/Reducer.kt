package com.f0x1d.logfox.core.tea

interface Reducer<State, Command, SideEffect> {
    fun reduce(state: State, command: Command): ReduceResult<State, SideEffect>
}
