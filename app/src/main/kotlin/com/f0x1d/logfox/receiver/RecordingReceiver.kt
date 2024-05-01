package com.f0x1d.logfox.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.f0x1d.logfox.BuildConfig
import com.f0x1d.logfox.repository.logging.RecordingsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecordingReceiver: BroadcastReceiver() {

    companion object {
        const val ACTION_PAUSE_LOGGING = "${BuildConfig.APPLICATION_ID}.PAUSE_LOGGING"
        const val ACTION_RESUME_LOGGING = "${BuildConfig.APPLICATION_ID}.RESUME_LOGGING"
        const val ACTION_STOP_LOGGING = "${BuildConfig.APPLICATION_ID}.STOP_LOGGING"
    }

    @Inject
    lateinit var recordingsRepository: RecordingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_PAUSE_LOGGING -> recordingsRepository.pause()
            ACTION_RESUME_LOGGING -> recordingsRepository.resume()
            ACTION_STOP_LOGGING -> recordingsRepository.end()
        }
    }
}