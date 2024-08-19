package com.f0x1d.logfox.arch.io

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun ZipOutputStream.putZipEntry(name: String, content: ByteArray) {
    val entry = ZipEntry(name)
    putNextEntry(entry)

    write(content, 0, content.size)
    closeEntry()
}

fun ZipOutputStream.putZipEntry(name: String, file: File) {
    val entry = ZipEntry(name)
    putNextEntry(entry)

    file.inputStream().use {
        it.copyTo(this)
    }

    closeEntry()
}
