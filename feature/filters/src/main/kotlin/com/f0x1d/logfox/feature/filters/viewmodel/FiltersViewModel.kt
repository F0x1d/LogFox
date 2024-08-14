package com.f0x1d.logfox.feature.filters.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.feature.filters.core.repository.FiltersRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val filtersRepository: FiltersRepository,
    private val gson: Gson,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel(application) {

    val filters = filtersRepository.getAllAsFlow()
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    fun import(uri: Uri) = launchCatching(ioDispatcher) {
        ctx.contentResolver.openInputStream(uri)?.use {
            val filters = gson.fromJson<List<UserFilter>>(
                it.readBytes().decodeToString(),
                object : TypeToken<List<UserFilter>>() { }.type
            )

            filtersRepository.createAll(filters)
        }
    }

    fun exportAll(uri: Uri) = launchCatching(ioDispatcher) {
        val filters = filters.value

        ctx.contentResolver.openOutputStream(uri)?.use {
            it.write(gson.toJson(filters).encodeToByteArray())
        }
    }

    fun switch(userFilter: UserFilter, checked: Boolean) = launchCatching {
        filtersRepository.switch(userFilter, checked)
    }

    fun delete(userFilter: UserFilter) = launchCatching {
        filtersRepository.delete(userFilter)
    }

    fun clearAll() = launchCatching {
        filtersRepository.clear()
    }
}
