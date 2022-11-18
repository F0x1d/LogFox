package com.f0x1d.logfox.ui.fragment.settings

import com.f0x1d.logfox.R
import com.f0x1d.logfox.ui.fragment.settings.base.BaseSettingsWrapperFragment
import com.f0x1d.logfox.ui.fragment.settings.base.SettingsNoActionsWrappedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsCrashesFragment: BaseSettingsWrapperFragment() {

    override val wrappedFragment get() = SettingsNoActionsWrappedFragment(R.xml.settings_crashes)
    override val title = R.string.crashes
    override val showBackArrow = true
}