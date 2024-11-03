package com.f0x1d.logfox.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TerminalResult(
    val exitCode: Int = 0,
    val output: String = "",
    val errorOutput: String = ""
): Parcelable {
    val isSuccessful get() = exitCode == 0
}
