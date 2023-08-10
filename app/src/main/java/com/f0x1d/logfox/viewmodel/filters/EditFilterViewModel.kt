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
import kotlinx.coroutines.flow.take

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
        .onEach {
            including = it?.including ?: return@onEach
            val allowedLevels = it.allowedLevels.map { it.ordinal }

            for (i in 0 until enabledLogLevels.size) {
                enabledLogLevels[i] = allowedLevels.contains(i)
            }
            pid = it.pid
            tid = it.tid
            tag = it.tag
            content = it.content
        }
        .asLiveData()

    var including = true
    val enabledLogLevels = mutableListOf(true, true, true, true, true, true, true)
    var pid: String? = null
    var tid: String? = null
    var tag: String? = null
    var content: String? = null

    fun create() = filtersRepository.create(
        including,
        enabledLogLevels.toEnabledLogLevels(),
        pid, tid, tag, content
    )

    fun update(userFilter: UserFilter) = filtersRepository.update(
        userFilter,
        including,
        enabledLogLevels.toEnabledLogLevels(),
        pid, tid, tag, content
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