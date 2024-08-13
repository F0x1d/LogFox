package com.f0x1d.logfox.feature.filters.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.arch.viewmodel.Event
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.feature.filters.core.repository.FiltersRepository
import com.f0x1d.logfox.feature.filters.di.FilterId
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.model.logline.LogLevel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EditFilterViewModel @Inject constructor(
    @FilterId val filterId: Long?,
    private val filtersRepository: FiltersRepository,
    private val gson: Gson,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel(application) {

    val filter = filtersRepository.getByIdAsFlow(filterId ?: -1L)
        .distinctUntilChanged()
        .take(1) // Not to handle changes
        .onEach { filter ->
            if (filter == null) return@onEach

            including.update { filter.including }

            val allowedLevels = filter.allowedLevels.map { it.ordinal }
            for (i in 0 until enabledLogLevels.size) {
                enabledLogLevels[i] = allowedLevels.contains(i)
            }

            uid.update { filter.uid }
            pid.update { filter.pid }
            tid.update { filter.tid }
            packageName.update { filter.packageName }
            tag.update { filter.tag }
            content.update { filter.content }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    val including = MutableStateFlow(true)
    val enabledLogLevels = mutableListOf(true, true, true, true, true, true, true)
    val uid = MutableStateFlow<String?>(null)
    val pid = MutableStateFlow<String?>(null)
    val tid = MutableStateFlow<String?>(null)
    val packageName = MutableStateFlow<String?>(null)
    val tag = MutableStateFlow<String?>(null)
    val content = MutableStateFlow<String?>(null)

    fun create() = launchCatching {
        filtersRepository.create(
            including.value,
            enabledLogLevels.toEnabledLogLevels(),
            uid.value, pid.value, tid.value, packageName.value, tag.value, content.value
        )
    }

    fun update(userFilter: UserFilter) = launchCatching {
        filtersRepository.update(
            userFilter,
            including.value,
            enabledLogLevels.toEnabledLogLevels(),
            uid.value, pid.value, tid.value, packageName.value, tag.value, content.value
        )
    }

    fun export(uri: Uri) = launchCatching(ioDispatcher) {
        ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val filters = filter.value?.let { listOf(it) } ?: emptyList()

            outputStream.write(gson.toJson(filters).encodeToByteArray())
        }
    }

    fun filterLevel(which: Int, filtering: Boolean) {
        enabledLogLevels[which] = filtering
    }

    fun selectApp(app: InstalledApp) = packageName.update {
        app.packageName
    }.also {
        sendEvent(UpdatePackageNameText)
    }

    private fun List<Boolean>.toEnabledLogLevels() = mapIndexed { index, value ->
        if (value)
            enumValues<LogLevel>()[index]
        else
            null
    }.filterNotNull()
}

data object UpdatePackageNameText : Event
