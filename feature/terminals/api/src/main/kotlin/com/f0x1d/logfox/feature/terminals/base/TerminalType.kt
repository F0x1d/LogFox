package com.f0x1d.logfox.feature.terminals.base

sealed interface TerminalType {
    val key: String

    data object Default : TerminalType {
        override val key = "default"
    }

    data object Root : TerminalType {
        override val key = "root"
    }

    data object Shizuku : TerminalType {
        override val key = "shizuku"
    }

    companion object {
        val entries: List<TerminalType> = listOf(Default, Root, Shizuku)

        fun fromKey(key: String): TerminalType = when (key) {
            Default.key -> Default
            Root.key -> Root
            Shizuku.key -> Shizuku
            else -> Default
        }
    }
}
