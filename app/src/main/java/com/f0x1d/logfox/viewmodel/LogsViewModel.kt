package com.f0x1d.logfox.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.f0x1d.logfox.di.NullString
import com.f0x1d.logfox.extensions.logline.filterAndSearch
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.f0x1d.logfox.repository.logging.LoggingRepository
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.f0x1d.logfox.viewmodel.base.BaseSameFlowProxyViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    application: Application,
    private val loggingRepository: LoggingRepository,
    private val filtersRepository: FiltersRepository,
    val appPreferences: AppPreferences,
    @NullString var query: String?
): BaseSameFlowProxyViewModel<List<LogLine>>(application, loggingRepository.logsFlow) {

    override val autoStartCollector = false

    val pausedData = MutableLiveData(false)
    val serviceRunningData = loggingRepository.serviceRunningFlow.asLiveData()

    val resumeLoggingWithBottomTouch get() = appPreferences.resumeLoggingWithBottomTouch

    init {
        restartCollector() // cool
    }

    override fun map(data: List<LogLine>?) = data?.filterAndSearch(filtersRepository, query)

    fun query(query: String?) {
        stopCollector()
        this.query = query
        recollect()
    }

    fun clearLogs() = loggingRepository.clearLogs()

    fun restartLogging() = loggingRepository.restartLoggingOnNewTerminal()

    fun recollect() = if (paused())
        collectOneValue()
    else
        restartCollector()

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