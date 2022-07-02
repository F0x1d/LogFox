package com.f0x1d.logfox.viewmodel.filters

import android.app.Application
import com.f0x1d.logfox.database.UserFilter
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(application: Application,
                                           private val filtersRepository: FiltersRepository): BaseSameFlowProxyViewModel<List<UserFilter>>(
    application,
    filtersRepository.filtersFlow
) {
    fun delete(userFilter: UserFilter) = filtersRepository.delete(userFilter)

    fun clearAll() = filtersRepository.clearFilters()
}