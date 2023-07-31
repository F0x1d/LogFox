package com.f0x1d.logfox.viewmodel.filters

import android.app.Application
import android.net.Uri
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.UserFilter
import com.f0x1d.logfox.model.LogLevel
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.f0x1d.logfox.utils.exportFilters
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers

class FilterViewModel @AssistedInject constructor(
    @Assisted filterId: Long,
    application: Application,
    database: AppDatabase,
    private val filtersRepository: FiltersRepository
): BaseSameFlowProxyViewModel<UserFilter>(application, database.userFilterDao().get(filterId)) {

    val enabledLogLevels = mutableListOf(true, true, true, true, true, true, true)

    override fun gotValue(data: UserFilter?) {
        val allowedLevels = data?.allowedLevels?.map { it.ordinal } ?: return

        for (i in 0 until enabledLogLevels.size) {
            enabledLogLevels[i] = allowedLevels.contains(i)
        }
    }

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
        ctx.contentResolver.openOutputStream(uri)?.exportFilters(ctx, data.value?.let { listOf(it) } ?: emptyList())
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