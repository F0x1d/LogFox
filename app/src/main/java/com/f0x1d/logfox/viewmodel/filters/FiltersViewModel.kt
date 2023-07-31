package com.f0x1d.logfox.viewmodel.filters

import android.app.Application
import android.net.Uri
import com.f0x1d.logfox.database.UserFilter
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.f0x1d.logfox.utils.exportFilters
import com.f0x1d.logfox.utils.importFilters
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    application: Application,
    private val filtersRepository: FiltersRepository
): BaseSameFlowProxyViewModel<List<UserFilter>>(application, filtersRepository.itemsFlow) {

    fun import(uri: Uri) = launchCatching(Dispatchers.IO) {
        ctx.contentResolver.openInputStream(uri)?.importFilters(ctx, filtersRepository)
    }

    fun exportAll(uri: Uri) = launchCatching(Dispatchers.IO) {
        ctx.contentResolver.openOutputStream(uri)?.exportFilters(ctx, filtersRepository.itemsFlow.value)
    }

    fun switch(userFilter: UserFilter, checked: Boolean) = filtersRepository.switch(userFilter, checked)
    fun delete(userFilter: UserFilter) = filtersRepository.delete(userFilter)
    fun clearAll() = filtersRepository.clear()
}