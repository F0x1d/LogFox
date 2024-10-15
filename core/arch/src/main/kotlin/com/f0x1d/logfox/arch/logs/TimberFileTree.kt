package com.f0x1d.logfox.arch.logs

import android.content.Context
import com.f0x1d.logfox.arch.di.IODispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimberFileTree @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : Timber.DebugTree() {

    private val logsFile = context.timberLogFile.apply { delete() }

    private val channel = Channel<String>(capacity = UNLIMITED)
    private val scope = CoroutineScope(ioDispatcher)

    init {
        scope.launch {
            for (value in channel) {
                logsFile.appendText(
                    text = value + "\n",
                )
            }
        }
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val exception = t?.stackTraceToString()?.let { "\n$it" } ?: ""
        val line = "${tag ?: "NO_TAG"}: $message" + exception

        channel.trySend(line)
    }
}

val Context.timberLogFile: File get() = File(filesDir, "timber.log")
