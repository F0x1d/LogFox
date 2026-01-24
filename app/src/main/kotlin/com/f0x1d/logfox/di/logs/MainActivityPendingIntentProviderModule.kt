package com.f0x1d.logfox.di.logs

import android.app.PendingIntent
import android.content.Context
import com.f0x1d.logfox.core.context.makeActivityPendingIntent
import com.f0x1d.logfox.feature.logging.service.presentation.MainActivityPendingIntentProvider
import com.f0x1d.logfox.presentation.ui.activity.MainActivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
internal interface MainActivityPendingIntentProviderModule {

    @Binds
    fun bindMainActivityPendingIntentProvider(
        mainActivityPendingIntentProviderImpl: MainActivityPendingIntentProviderImpl,
    ): MainActivityPendingIntentProvider
}

internal class MainActivityPendingIntentProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : MainActivityPendingIntentProvider {
    override fun provide(id: Int): PendingIntent = context.makeActivityPendingIntent<MainActivity>(
        id = id,
    )
}
