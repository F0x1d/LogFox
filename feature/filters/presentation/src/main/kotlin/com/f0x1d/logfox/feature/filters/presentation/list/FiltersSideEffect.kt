package com.f0x1d.logfox.feature.filters.presentation.list

import android.net.Uri
import com.f0x1d.logfox.feature.database.model.UserFilter

sealed interface FiltersSideEffect {
    // Business logic side effects - handled by EffectHandler
    data object LoadFilters : FiltersSideEffect
    data class ImportFilters(val uri: Uri) : FiltersSideEffect
    data class ExportAllFilters(val uri: Uri, val filters: List<UserFilter>) : FiltersSideEffect
    data class SwitchFilter(val filter: UserFilter, val checked: Boolean) : FiltersSideEffect
    data class DeleteFilter(val filter: UserFilter) : FiltersSideEffect
    data object ClearAllFilters : FiltersSideEffect
}
