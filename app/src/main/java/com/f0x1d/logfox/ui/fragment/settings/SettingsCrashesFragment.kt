package com.f0x1d.logfox.ui.fragment.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.Preference
import com.f0x1d.logfox.R
import com.f0x1d.logfox.extensions.context.catchingNotNumber
import com.f0x1d.logfox.extensions.views.widgets.observeAndUpdateSummary
import com.f0x1d.logfox.extensions.views.widgets.setupAsEditTextPreference
import com.f0x1d.logfox.ui.fragment.settings.base.BasePreferenceFragment
import com.f0x1d.logfox.utils.preferences.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsCrashesFragment: BasePreferenceFragment() {

    override val title = R.string.crashes
    override val showBackArrow = true

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_crashes)

        findPreference<Preference>("pref_logs_dump_lines_count")?.apply {
            setupAsEditTextPreference({
                it.textLayout.hint = "0...1000"
                it.text.inputType = InputType.TYPE_CLASS_NUMBER
            }, {
                setIcon(R.drawable.ic_dialog_list)
            }, {
                appPreferences.logsDumpLinesCount.toString()
            }, {
                requireContext().catchingNotNumber {
                    val count = it?.toInt() ?: AppPreferences.LOGS_DUMP_LINES_COUNT_DEFAULT

                    appPreferences.logsDumpLinesCount = count.coerceIn(0..1000)
                }
            })

            observeAndUpdateSummary(appPreferences, this@SettingsCrashesFragment, AppPreferences.LOGS_DUMP_LINES_COUNT_DEFAULT)
        }
    }
}