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
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun InputStream.importFilters(context: Context, filtersRepository: FiltersRepository) {
    val gson = EntryPointAccessors.fromApplication(context, ExportImportUtilsEntryPoint::class.java).gson()

    val filters = gson.fromJson<List<UserFilter>>(String(readBytes()), object : TypeToken<List<UserFilter>>() {}.type)
    filtersRepository.createAll(filters)
}

fun OutputStream.exportFilters(context: Context, filters: List<UserFilter>) {
    val gson = EntryPointAccessors.fromApplication(context, ExportImportUtilsEntryPoint::class.java).gson()

    write(gson.toJson(filters).encodeToByteArray())
    close()
}

fun OutputStream.exportLogToZip(context: Context, log: String) {
    val device = EntryPointAccessors.fromApplication(context, ExportImportUtilsEntryPoint::class.java).device()

    ZipOutputStream(this).apply {
        putZipEntry("log.txt", log.encodeToByteArray())
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
interface ExportImportUtilsEntryPoint {
    fun device(): Device
    fun gson(): Gson
}