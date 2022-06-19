package com.f0x1d.logfox.extensions

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.utils.exportLogToZip
import com.f0x1d.logfox.viewmodel.base.BaseFlowProxyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

inline fun <T, R> BaseFlowProxyViewModel<T, R>.logToZip(uri: Uri, crossinline block: R.() -> String) {
    viewModelScope.launch(Dispatchers.IO) {
        data.value?.apply {
            ctx.contentResolver.openOutputStream(uri)?.exportLogToZip(ctx, block.invoke(this))
        }
    }
}