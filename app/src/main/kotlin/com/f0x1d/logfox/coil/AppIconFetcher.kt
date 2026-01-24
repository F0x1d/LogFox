package com.f0x1d.logfox.coil

import android.content.Context
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class AppIconFetcher(
    private val context: Context,
    private val app: InstalledApp,
    private val ioDispatcher: CoroutineDispatcher,
) : Fetcher {

    override suspend fun fetch(): FetchResult = withContext(ioDispatcher) {
        val appDrawable = context.packageManager.getApplicationIcon(app.packageName)

        DrawableResult(
            drawable = appDrawable,
            isSampled = true,
            dataSource = DataSource.DISK,
        )
    }

    @Singleton
    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : Fetcher.Factory<InstalledApp> {
        override fun create(
            data: InstalledApp,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher = AppIconFetcher(
            context = context,
            app = data,
            ioDispatcher = ioDispatcher,
        )
    }
}
