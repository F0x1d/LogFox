package com.f0x1d.logfox.extensions

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.utils.exportCrashToZip
import com.f0x1d.logfox.utils.exportLogToZip
import com.f0x1d.logfox.viewmodel.base.BaseFlowProxyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream

inline fun <T, R> BaseFlowProxyViewModel<T, R>.logToZip(uri: Uri, crossinline block: R.() -> String) = toZip(uri) {
    exportLogToZip(ctx, block.invoke(it))
}

inline fun <T, R> BaseFlowProxyViewModel<T, R>.crashToZip(uri: Uri, crossinline block: R.() -> String) = toZip(uri) {
    exportCrashToZip(ctx, block.invoke(it))
}

inline fun <T, R> BaseFlowProxyViewModel<T, R>.toZip(uri: Uri, crossinline block: OutputStream.(R) -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
        data.value?.apply {
            ctx.contentResolver.openOutputStream(uri)?.also {
                block.invoke(it, this)
            }
        }
    }
}