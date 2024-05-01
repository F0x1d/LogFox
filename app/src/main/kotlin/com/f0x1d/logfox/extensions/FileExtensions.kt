package com.f0x1d.logfox.extensions

import android.content.Context
import androidx.core.content.FileProvider
import com.f0x1d.logfox.BuildConfig
import java.io.File

fun File.asUri(context: Context) = FileProvider.getUriForFile(
    context,
    "${BuildConfig.APPLICATION_ID}.provider",
    this
)