package com.f0x1d.logfox.feature.filters.presentation.edit.di

data class EditFilterArgs(
    val filterId: Long?,
    val uid: String?,
    val pid: String?,
    val tid: String?,
    val packageName: String?,
    val tag: String?,
    val content: String?,
    val level: Int?,
) {
    val hasValidFilterId: Boolean
        get() = filterId != null && filterId != -1L

    val hasInitialData: Boolean
        get() = uid != null || pid != null || tid != null ||
            packageName != null || tag != null || content != null ||
            (level != null && level >= 0)
}
