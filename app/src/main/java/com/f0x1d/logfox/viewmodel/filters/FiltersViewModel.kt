package com.f0x1d.logfox.viewmodel.filters

import android.app.Application
import android.net.Uri
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.UserFilter
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.f0x1d.logfox.utils.exportFilters
import com.f0x1d.logfox.utils.importFilters
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    application: Application,
    private val database: AppDatabase,
    private val filtersRepository: FiltersRepository
): BaseViewModel(application) {

    val filters = database.userFilterDao().getAllAsFlow()
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .asLiveData()

    fun import(uri: Uri) = launchCatching(Dispatchers.IO) {
        ctx.contentResolver.openInputStream(uri)?.importFilters(ctx, filtersRepository)
    }

    fun exportAll(uri: Uri) = launchCatching(Dispatchers.IO) {
        ctx.contentResolver.openOutputStream(uri)?.exportFilters(ctx, filters.value ?: return@launchCatching)
    }

    fun switch(userFilter: UserFilter, checked: Boolean) = filtersRepository.switch(userFilter, checked)
    fun delete(userFilter: UserFilter) = filtersRepository.delete(userFilter)
    fun clearAll() = filtersRepository.clear()
}