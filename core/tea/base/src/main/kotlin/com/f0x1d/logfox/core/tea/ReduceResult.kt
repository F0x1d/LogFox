package com.f0x1d.logfox.core.tea

data class ReduceResult<State, SideEffect>(
    val state: State,
    val sideEffects: List<SideEffect> = emptyList(),
)

fun <State, SideEffect> State.withSideEffects(
    vararg sideEffects: SideEffect,
): ReduceResult<State, SideEffect> = ReduceResult(this, sideEffects.toList())

fun <State, SideEffect> State.noSideEffects(): ReduceResult<State, SideEffect> = ReduceResult(this, emptyList())
