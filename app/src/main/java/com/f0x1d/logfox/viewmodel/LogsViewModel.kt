package com.f0x1d.logfox.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.f0x1d.logfox.extensions.filterAndSearch
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogsViewModel(application: Application,
                    private val loggingRepository: LoggingRepository,
                    private val filtersRepository: FiltersRepository,
                    var query: String?): BaseSameFlowProxyViewModel<List<LogLine>>(
    application,
    loggingRepository.logsFlow
) {

    @Inject
    constructor(application: Application, loggingRepository: LoggingRepository, filtersRepository: FiltersRepository):
            this(application, loggingRepository, filtersRepository, null)

    val pausedData = MutableLiveData(false)

    override fun map(data: List<LogLine>?) = data?.filterAndSearch(filtersRepository, query)

    fun query(query: String?) {
        stopCollector()
        this.query = query
        recollect()
    }

    fun clearLogs() {
        loggingRepository.clearLogs()
    }

    fun recollect() {
        if (paused())
            collectOneValue()
        else
            restartCollector()
    }

    fun paused() = pausedData.value == true
    fun switchState() = if (!pausedData.value!!)
        pause()
    else
        resume()

    fun pause() {
        stopCollector()
        pausedData.value = true
    }
    fun resume() {
        restartCollector()
        pausedData.value = false
    }
}