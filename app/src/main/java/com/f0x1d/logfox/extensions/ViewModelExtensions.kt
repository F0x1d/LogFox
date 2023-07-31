package com.f0x1d.logfox.extensions

import android.net.Uri
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.utils.exportCrashToZip
import com.f0x1d.logfox.utils.exportLogToZip
import com.f0x1d.logfox.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import java.io.OutputStream

fun BaseViewModel.logToZip(uri: Uri, logRecording: LogRecording) = toZip(uri) {
    exportLogToZip(ctx, logRecording.file)
}

fun BaseViewModel.crashToZip(uri: Uri, appCrash: AppCrash) = toZip(uri) {
    exportCrashToZip(ctx, appCrash.log)
}

inline fun BaseViewModel.toZip(uri: Uri, crossinline block: OutputStream.() -> Unit) {
    launchCatching(Dispatchers.IO) {
        ctx.contentResolver.openOutputStream(uri)?.also {
            block.invoke(it)
        }
    }
}