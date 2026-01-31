package com.f0x1d.logfox.feature.logging.impl.data

import android.content.Context
import android.net.Uri
import com.f0x1d.logfox.core.di.IODispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LogExportDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : LogExportDataSource {

    override suspend fun exportToUri(content: String, uri: Uri): Unit = withContext(ioDispatcher) {
        context.contentResolver.openOutputStream(uri)?.use {
            it.write(content.encodeToByteArray())
        }
    }
}
