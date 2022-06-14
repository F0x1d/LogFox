package com.f0x1d.logfox.utils

import android.content.Context
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.model.Device
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun OutputStream.exportLogToZip(context: Context, appCrash: AppCrash) {
    val device = EntryPointAccessors.fromApplication(context, ExportUtilsEntryPoint::class.java).device()

    ZipOutputStream(this).apply {
        putZipEntry("log.txt", appCrash.log.encodeToByteArray())
        putZipEntry("device.txt", device.toString().encodeToByteArray())

        close()
    }
}

private fun ZipOutputStream.putZipEntry(name: String, content: ByteArray) {
    val entry = ZipEntry(name)
    putNextEntry(entry)

    write(content, 0, content.size)
    closeEntry()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ExportUtilsEntryPoint {
    fun device(): Device
}