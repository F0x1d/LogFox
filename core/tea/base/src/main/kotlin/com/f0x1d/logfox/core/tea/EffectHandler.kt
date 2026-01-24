package com.f0x1d.logfox.core.tea

import java.io.Closeable

interface EffectHandler<SideEffect, Command>: Closeable {
    suspend fun handle(effect: SideEffect, onCommand: suspend (Command) -> Unit)

    override fun close() = Unit
}
