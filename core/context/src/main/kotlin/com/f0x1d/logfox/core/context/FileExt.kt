package com.f0x1d.logfox.core.context

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun File.asUri(context: Context): Uri = FileProvider.getUriForFile(
    context,
    "${context.packageName}.provider",
    this,
)
