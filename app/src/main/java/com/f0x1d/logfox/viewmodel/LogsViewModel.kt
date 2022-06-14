package com.f0x1d.logfox.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.f0x1d.logfox.extensions.filterAndSearch
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.LoggingRepository
import com.f0x1d.logfox.utils.preferences.EnabledLogLevels
import com.f0x1d.logfox.utils.preferences.LogFilterPreferences
import com.f0x1d.logfox.viewmodel.base.BaseFlowProxyViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogsViewModel(application: Application,
                    private val logFilterPreferences: LogFilterPreferences,
                    private val loggingRepository: LoggingRepository,
                    var query: String?,
                    val currentEnabledLogLevels: EnabledLogLevels): BaseFlowProxyViewModel<List<LogLine>, List<LogLine>>(
    application,
    loggingRepository.logsFlow
) {

    @Inject constructor(application: Application, logFilterPreferences: LogFilterPreferences, loggingRepository: LoggingRepository):
            this(application, logFilterPreferences, loggingRepository, null, logFilterPreferences.currentEnabledLogLevels)

    val pausedData = MutableLiveData(false)

    override fun map(data: List<LogLine>?) = data?.filterAndSearch(query, currentEnabledLogLevels)

    fun query(query: String?) {
        stopCollector()
        this.query = query
        recollect()
    }

    fun filterLevel(which: Int, filtering: Boolean) {
        stopCollector()
        when (which) {
            0 -> logFilterPreferences.verboseEnabled = filtering.also { currentEnabledLogLevels.verboseEnabled = it }
            1 -> logFilterPreferences.debugEnabled = filtering.also { currentEnabledLogLevels.debugEnabled = it }
            2 -> logFilterPreferences.infoEnabled = filtering.also { currentEnabledLogLevels.infoEnabled = it }
            3 -> logFilterPreferences.warningEnabled = filtering.also { currentEnabledLogLevels.warningEnabled = it }
            4 -> logFilterPreferences.errorEnabled = filtering.also { currentEnabledLogLevels.errorEnabled = it }
            5 -> logFilterPreferences.fatalEnabled = filtering.also { currentEnabledLogLevels.fatalEnabled = it }
            6 -> logFilterPreferences.silentEnabled = filtering.also { currentEnabledLogLevels.silentEnabled = it }
        }
        recollect()
    }

    fun clearLogs() {
        loggingRepository.clearLogs()
    }

    private fun recollect() {
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