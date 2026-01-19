package com.f0x1d.logfox.core.copy

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.f0x1d.logfox.core.context.toast
import com.f0x1d.logfox.feature.strings.Strings

fun Context.copyText(text: String) = runCatching {
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(ClipData.newPlainText("LogFox", text))
}.onFailure {
    toast(Strings.error)
}
