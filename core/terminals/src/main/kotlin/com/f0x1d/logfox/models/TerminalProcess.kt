package com.f0x1d.logfox.models

import java.io.InputStream
import java.io.OutputStream

data class TerminalProcess(
    val output: InputStream,
    val error: InputStream,
    val input: OutputStream,
    val destroy: () -> Unit,
)
