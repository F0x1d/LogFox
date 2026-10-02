package com.f0x1d.logfox.core.io

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun ZipOutputStream.putZipEntry(name: String, content: ByteArray) {
    val entry = ZipEntry(name)
    putNextEntry(entry)

    try {
        write(content, 0, content.size)
    } finally {
        runCatching { closeEntry() }
    }
}

fun ZipOutputStream.putZipEntry(name: String, file: File) {
    val entry = ZipEntry(name)
    putNextEntry(entry)

    try {
        file.inputStream().use {
            it.copyTo(this)
        }
    } finally {
        runCatching { closeEntry() }
    }
}
