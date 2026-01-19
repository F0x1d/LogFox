package com.f0x1d.logfox.core.tea

interface EffectHandler<SideEffect, Command> {
    suspend fun handle(effect: SideEffect, onCommand: suspend (Command) -> Unit)
}
