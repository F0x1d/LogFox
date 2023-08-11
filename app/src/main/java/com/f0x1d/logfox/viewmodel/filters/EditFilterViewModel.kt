package com.f0x1d.logfox.viewmodel.filters

import android.app.Application
import android.net.Uri
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.f0x1d.logfox.utils.exportFilters
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update

class EditFilterViewModel @AssistedInject constructor(
    @Assisted filterId: Long,
    application: Application,
    private val database: AppDatabase,
    private val filtersRepository: FiltersRepository
): BaseViewModel(application) {

    val filter = database.userFilterDao().get(filterId)
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

            pid.update { filter.pid }
            tid.update { filter.tid }
            tag.update { filter.tag }
            content.update { filter.content }
        }
        .asLiveData()

    val including = MutableStateFlow(true)
    val enabledLogLevels = mutableListOf(true, true, true, true, true, true, true)
    val pid = MutableStateFlow<String?>(null)
    val tid = MutableStateFlow<String?>(null)
    val tag = MutableStateFlow<String?>(null)
    val content = MutableStateFlow<String?>(null)

    fun create() = filtersRepository.create(
        including.value,
        enabledLogLevels.toEnabledLogLevels(),
        pid.value, tid.value, tag.value, content.value
    )

    fun update(userFilter: UserFilter) = filtersRepository.update(
        userFilter,
        including.value,
        enabledLogLevels.toEnabledLogLevels(),
        pid.value, tid.value, tag.value, content.value
    )

    fun export(uri: Uri) = launchCatching(Dispatchers.IO) {
        ctx.contentResolver.openOutputStream(uri)?.exportFilters(ctx, filter.value?.let { listOf(it) } ?: emptyList())
    }

    fun filterLevel(which: Int, filtering: Boolean) {
        enabledLogLevels[which] = filtering
    }

    private fun List<Boolean>.toEnabledLogLevels() = mapIndexed { index, value -> if (value) enumValues<LogLevel>()[index] else null }.filterNotNull()
}

@AssistedFactory
interface EditFilterViewModelAssistedFactory {
    fun create(filterId: Long): EditFilterViewModel
}