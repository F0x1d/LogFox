package com.f0x1d.logfox.feature.logging.presentation.search

sealed interface SearchLogsSideEffect {
    // Business logic side effects - handled by EffectHandler
    data object LoadQuery : SearchLogsSideEffect

    data class SaveQuery(val query: String?) : SearchLogsSideEffect

    data object LoadCaseSensitive : SearchLogsSideEffect

    data class SaveCaseSensitive(val caseSensitive: Boolean) : SearchLogsSideEffect

    // UI side effects - handled by Fragment
    data object Dismiss : SearchLogsSideEffect
}
