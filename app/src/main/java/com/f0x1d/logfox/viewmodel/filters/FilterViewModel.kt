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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

class FilterViewModel @AssistedInject constructor(
    @Assisted filterId: Long,
    application: Application,
    private val database: AppDatabase,
    private val filtersRepository: FiltersRepository
): BaseViewModel(application) {

    val filter = database.userFilterDao().get(filterId)
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .onEach {
            val allowedLevels = it?.allowedLevels?.map { it.ordinal } ?: return@onEach

            for (i in 0 until enabledLogLevels.size) {
                enabledLogLevels[i] = allowedLevels.contains(i)
            }
        }
        .asLiveData()

    val enabledLogLevels = mutableListOf(true, true, true, true, true, true, true)

    fun create(filterTextData: FilterTextData) = filtersRepository.create(
        enabledLogLevels.toEnabledLogLevels(),
        filterTextData.pid,
        filterTextData.tid,
        filterTextData.tag,
        filterTextData.content
    )

    fun update(userFilter: UserFilter, filterTextData: FilterTextData) = filtersRepository.update(
        userFilter,
        enabledLogLevels.toEnabledLogLevels(),
        filterTextData.pid,
        filterTextData.tid,
        filterTextData.tag,
        filterTextData.content
    )

    fun export(uri: Uri) = launchCatching(Dispatchers.IO) {
        ctx.contentResolver.openOutputStream(uri)?.exportFilters(ctx, filter.value?.let { listOf(it) } ?: emptyList())
    }

    fun filterLevel(which: Int, filtering: Boolean) {
        enabledLogLevels[which] = filtering
    }

    private fun List<Boolean>.toEnabledLogLevels() = mapIndexed { index, value -> if (value) enumValues<LogLevel>()[index] else null }.filterNotNull()
}

data class FilterTextData(val pid: String, val tid: String, val tag: String, val content: String)

@AssistedFactory
interface FilterViewModelAssistedFactory {
    fun create(filterId: Long): FilterViewModel
}