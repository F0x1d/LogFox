package com.f0x1d.logfox.viewmodel.filters

import android.app.Application
import android.net.Uri
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.di.viewmodel.FilterId
import com.f0x1d.logfox.extensions.sendEvent
import com.f0x1d.logfox.model.InstalledApp
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EditFilterViewModel @Inject constructor(
    @FilterId val filterId: Long?,
    private val database: AppDatabase,
    private val filtersRepository: FiltersRepository,
    private val gson: Gson,
    application: Application
): BaseViewModel(application) {

    companion object {
        const val EVENT_TYPE_UPDATE_PACKAGE_NAME_TEXT = "update_package_name_text"
    }

    val filter = database.userFilterDao().get(filterId ?: -1L)
        .distinctUntilChanged()
        .take(1) // Not to handle changes
        .flowOn(Dispatchers.IO)
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
        .asLiveData()

    val including = MutableStateFlow(true)
    val enabledLogLevels = mutableListOf(true, true, true, true, true, true, true)
    val uid = MutableStateFlow<String?>(null)
    val pid = MutableStateFlow<String?>(null)
    val tid = MutableStateFlow<String?>(null)
    val packageName = MutableStateFlow<String?>(null)
    val tag = MutableStateFlow<String?>(null)
    val content = MutableStateFlow<String?>(null)

    fun create() = filtersRepository.create(
        including.value,
        enabledLogLevels.toEnabledLogLevels(),
        uid.value, pid.value, tid.value, packageName.value, tag.value, content.value
    )

    fun update(userFilter: UserFilter) = filtersRepository.update(
        userFilter,
        including.value,
        enabledLogLevels.toEnabledLogLevels(),
        uid.value, pid.value, tid.value, packageName.value, tag.value, content.value
    )

    fun export(uri: Uri) = launchCatching(Dispatchers.IO) {
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
        sendEvent(EVENT_TYPE_UPDATE_PACKAGE_NAME_TEXT)
    }

    private fun List<Boolean>.toEnabledLogLevels() = mapIndexed { index, value ->
        if (value)
            enumValues<LogLevel>()[index]
        else
            null
    }.filterNotNull()
}