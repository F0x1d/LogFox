package com.f0x1d.logfox.feature.apps.picker.presentation

import android.content.Context
import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.core.tea.EffectHandler
import com.f0x1d.logfox.feature.apps.picker.InstalledApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AppsPickerEffectHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : EffectHandler<AppsPickerSideEffect, AppsPickerCommand> {

    override suspend fun handle(
        effect: AppsPickerSideEffect,
        onCommand: suspend (AppsPickerCommand) -> Unit,
    ) {
        when (effect) {
            is AppsPickerSideEffect.LoadApps -> {
                val apps = withContext(defaultDispatcher) {
                    loadInstalledApps()
                }
                onCommand(AppsPickerCommand.AppsLoaded(apps))
            }

            is AppsPickerSideEffect.FilterApps -> {
                val filteredApps = withContext(defaultDispatcher) {
                    filterApps(effect.query, effect.apps)
                }
                onCommand(AppsPickerCommand.SearchedAppsUpdated(filteredApps))
            }

            // UI side effects are handled by Fragment
            is AppsPickerSideEffect.PopBackStack -> Unit
        }
    }

    private fun loadInstalledApps() = context.packageManager
        .getInstalledPackages(0)
        .map { packageInfo ->
            InstalledApp(
                title = packageInfo.applicationInfo?.loadLabel(context.packageManager).toString(),
                packageName = packageInfo.packageName,
            )
        }
        .sortedBy(InstalledApp::title)
        .toImmutableList()

    private fun filterApps(
        query: String,
        apps: kotlinx.collections.immutable.ImmutableList<InstalledApp>,
    ) = apps.filter { app ->
        app.title.contains(query, ignoreCase = true)
                || app.packageName.contains(query, ignoreCase = true)
    }.toImmutableList()
}
