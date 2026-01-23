package com.f0x1d.logfox.feature.filters.presentation.list

import android.net.Uri
import com.f0x1d.logfox.feature.database.model.UserFilter

sealed interface FiltersCommand {
    data object Load : FiltersCommand
    data class FiltersLoaded(val filters: List<UserFilter>) : FiltersCommand
    data class Import(val uri: Uri) : FiltersCommand
    data class ExportAll(val uri: Uri) : FiltersCommand
    data class Switch(val filter: UserFilter, val checked: Boolean) : FiltersCommand
    data class Delete(val filter: UserFilter) : FiltersCommand
    data object ClearAll : FiltersCommand
    data class OpenFilter(val filterId: Long) : FiltersCommand
    data object CreateNewFilter : FiltersCommand
}
