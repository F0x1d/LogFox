package com.f0x1d.logfox.arch.io

import java.io.OutputStream
import java.util.zip.ZipOutputStream

fun OutputStream.exportToZip(block: ZipOutputStream.() -> Unit) = use {
    ZipOutputStream(this).use { zipOutputStream ->
        block(zipOutputStream)
    }
}
