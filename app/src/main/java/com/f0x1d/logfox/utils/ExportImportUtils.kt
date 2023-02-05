package com.f0x1d.logfox.utils

import android.content.Context
import com.f0x1d.logfox.database.UserFilter
import com.f0x1d.logfox.model.Device
import com.f0x1d.logfox.repository.logging.FiltersRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun InputStream.importFilters(context: Context, filtersRepository: FiltersRepository) {
    val gson = EntryPointAccessors.fromApplication(context, ExportImportUtilsEntryPoint::class.java).gson()

    val filters = gson.fromJson<List<UserFilter>>(String(readBytes()), object : TypeToken<List<UserFilter>>() {}.type)
    filtersRepository.createAll(filters)

    close()
}

fun OutputStream.exportFilters(context: Context, filters: List<UserFilter>) {
    val gson = EntryPointAccessors.fromApplication(context, ExportImportUtilsEntryPoint::class.java).gson()

    write(gson.toJson(filters).encodeToByteArray())

    close()
}

fun OutputStream.exportCrashToZip(context: Context, log: String) = exportToZip(context) {
    putZipEntry("log.txt", log.encodeToByteArray())
}

fun OutputStream.exportLogToZip(context: Context, logFile: String) = exportToZip(context) {
    putZipEntry("log.txt", File(logFile))
}

private fun OutputStream.exportToZip(context: Context, block: ZipOutputStream.() -> Unit) {
    val device = EntryPointAccessors.fromApplication(context, ExportImportUtilsEntryPoint::class.java).device()

    ZipOutputStream(this).apply {
        block.invoke(this)
        putZipEntry("device.txt", device.toString().encodeToByteArray())

        close()
    }

    close()
}

private fun ZipOutputStream.putZipEntry(name: String, content: ByteArray) {
    val entry = ZipEntry(name)
    putNextEntry(entry)

    write(content, 0, content.size)
    closeEntry()
}

private fun ZipOutputStream.putZipEntry(name: String, file: File) {
    val entry = ZipEntry(name)
    putNextEntry(entry)

    with(FileInputStream(file)) {
        copyTo(this@putZipEntry)
        close()
    }

    closeEntry()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ExportImportUtilsEntryPoint {
    fun device(): Device
    fun gson(): Gson
}