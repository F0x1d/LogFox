package com.f0x1d.logfox.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.extensions.filterEnabledLines
import com.f0x1d.logfox.logging.Logging
import com.f0x1d.logfox.logging.model.LogLine
import com.f0x1d.logfox.utils.preferences.LogFilterPreferences
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(application: Application, private val logFilterPreferences: LogFilterPreferences): BaseViewModel(application) {

    val pausedData = MutableLiveData(false)
    val logsData = MutableLiveData<List<LogLine>>()
    val distinctiveLogsData = logsData.distinctUntilChanged()

    var query: String? = null
        set(value) {
            field = value
            startCollector()
        }

    private var currentJob: Job? = null
    val currentEnabledLogLevels = logFilterPreferences.currentEnabledLogLevels

    init {
        startCollector()
    }

    private fun startCollector() {
        currentJob?.cancel()

        currentJob = viewModelScope.launch(Dispatchers.Default) {
            Logging.logsFlow.map {
                it.filterEnabledLines(currentEnabledLogLevels).let {
                    if (query == null)
                        it
                    else
                        it.filter { it.tag.contains(query ?: "") || it.content.contains(query ?: "") }
                }
            }.collect {
                logsData.postValue(it)
            }
        }
    }

    fun filterLevel(which: Int, filtering: Boolean) {
        when (which) {
            0 -> logFilterPreferences.verboseEnabled = filtering.also { currentEnabledLogLevels.verboseEnabled = it }
            1 -> logFilterPreferences.debugEnabled = filtering.also { currentEnabledLogLevels.debugEnabled = it }
            2 -> logFilterPreferences.infoEnabled = filtering.also { currentEnabledLogLevels.infoEnabled = it }
            3 -> logFilterPreferences.warningEnabled = filtering.also { currentEnabledLogLevels.warningEnabled = it }
            4 -> logFilterPreferences.errorEnabled = filtering.also { currentEnabledLogLevels.errorEnabled = it }
            5 -> logFilterPreferences.fatalEnabled = filtering.also { currentEnabledLogLevels.fatalEnabled = it }
            6 -> logFilterPreferences.silentEnabled = filtering.also { currentEnabledLogLevels.silentEnabled = it }
        }
        startCollector()
    }

    fun paused() = pausedData.value == true
    fun switchState() {
        pausedData.value = !pausedData.value!!
    }
    fun pause() {
        pausedData.value = true
    }
    fun resume() {
        pausedData.value = false
    }
}