package com.f0x1d.logfox.model.terminal.shizuku

import android.os.ParcelFileDescriptor
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShizukuTerminalProcess(
    val processId: Long,
    val output: ParcelFileDescriptor,
    val error: ParcelFileDescriptor,
    val input: ParcelFileDescriptor
): Parcelable