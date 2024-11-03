package com.f0x1d.logfox.feature.logging.search.presentation

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.feature.logging.api.data.QueryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchLogsViewModel @Inject constructor(
    private val queryDataSource: QueryDataSource,
    application: Application,
) : BaseViewModel<SearchLogsState, SearchLogsAction>(
    initialStateProvider = { SearchLogsState() },
    application = application,
) {
    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            queryDataSource.query.collect { query ->
                reduce { copy(query = query) }
            }
        }
    }

    fun updateQuery(query: String?) {
        viewModelScope.launch {
            queryDataSource.updateQuery(query)
            sendAction(SearchLogsAction.Dismiss)
        }
    }
}
