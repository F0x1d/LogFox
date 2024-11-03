package com.f0x1d.logfox.feature.filters.edit.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.feature.apps.picker.AppsPickerResultHandler
import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.model.logline.LogLevel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditFilterViewModel @Inject constructor(
    @com.f0x1d.logfox.feature.filters.edit.di.FilterId val filterId: Long?,
    private val filtersRepository: FiltersRepository,
    private val gson: Gson,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel<EditFilterState, EditFilterAction>(
    initialStateProvider = { EditFilterState() },
    application = application,
), AppsPickerResultHandler {
    var uid: String? = null
    var pid: String? = null
    var tid: String? = null
    var packageName: String? = null
    var tag: String? = null
    var content: String? = null

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            filtersRepository.getByIdAsFlow(filterId ?: -1L)
                .distinctUntilChanged()
                .take(1) // Not to handle changes
                .collect { filter ->
                    if (filter == null) return@collect

                    val enabledLogLevels = List(7) { false }.toMutableList()
                    val allowedLevels = filter.allowedLevels.map { it.ordinal }
                    for (i in 0 until enabledLogLevels.size) {
                        enabledLogLevels[i] = allowedLevels.contains(i)
                    }

                    uid = filter.uid
                    pid = filter.pid
                    tid = filter.tid
                    packageName = filter.packageName
                    tag = filter.tag
                    content = filter.content

                    reduce {
                        copy(
                            filter = filter,
                            including = filter.including,
                            enabledLogLevels = enabledLogLevels,
                        )
                    }
                }
        }
    }

    fun save() = launchCatching {
        val state = currentState

        if (state.filter == null) {
            filtersRepository.create(
                including = state.including,
                enabledLogLevels = state.enabledLogLevels.toEnabledLogLevels(),
                uid = uid,
                pid = pid,
                tid = tid,
                packageName = packageName,
                tag = tag,
                content = content,
            )
        } else {
            filtersRepository.update(
                userFilter = state.filter,
                including = state.including,
                enabledLogLevels = state.enabledLogLevels.toEnabledLogLevels(),
                uid = uid,
                pid = pid,
                tid = tid,
                packageName = packageName,
                tag = tag,
                content = content,
            )
        }
    }

    fun export(uri: Uri) = launchCatching(ioDispatcher) {
        ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val filters = listOfNotNull(currentState.filter)

            outputStream.write(gson.toJson(filters).encodeToByteArray())
        }
    }

    fun toggleIncluding() = reduce { copy(including = including.not()) }

    fun filterLevel(which: Int, filtering: Boolean) = reduce {
        copy(
            enabledLogLevels = enabledLogLevels.toMutableList().apply {
                this[which] = filtering
            },
        )
    }

    override fun onAppSelected(app: InstalledApp): Boolean {
        packageName = app.packageName
        sendAction(EditFilterAction.UpdatePackageNameText(app.packageName))
        return true
    }

    private fun List<Boolean>.toEnabledLogLevels() = mapIndexed { index, value ->
        if (value)
            enumValues<LogLevel>()[index]
        else
            null
    }.filterNotNull()
}
