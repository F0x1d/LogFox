package com.f0x1d.logfox.feature.preferences.presentation.menu

import android.content.Context
import com.f0x1d.logfox.core.tea.BaseStoreViewModel
import com.f0x1d.logfox.core.tea.ViewStateMapper
import com.f0x1d.logfox.feature.preferences.presentation.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
internal class PreferencesMenuViewModel @Inject constructor(
    @ApplicationContext context: Context,
    reducer: PreferencesMenuReducer,
) : BaseStoreViewModel<PreferencesMenuState, PreferencesMenuState, PreferencesMenuCommand, PreferencesMenuSideEffect>(
    initialState = run {
        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
        PreferencesMenuState(
            versionName = packageInfo.versionName ?: "",
            versionCode = packageInfo.versionCode,
            isDebug = BuildConfig.DEBUG,
        )
    },
    reducer = reducer,
    effectHandlers = emptyList(),
    viewStateMapper = ViewStateMapper.identity(),
)
