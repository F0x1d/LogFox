package com.f0x1d.logfox.feature.filters.edit.presentation

sealed interface EditFilterAction {
    data class UpdatePackageNameText(val packageName: String) : EditFilterAction
}
