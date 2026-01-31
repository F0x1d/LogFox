package com.f0x1d.logfox.feature.preferences.presentation.ui.settings.ui

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import com.f0x1d.logfox.core.compat.monetAvailable
import com.f0x1d.logfox.core.context.catchingNotNumber
import com.f0x1d.logfox.core.context.isHorizontalOrientation
import com.f0x1d.logfox.core.tea.BaseStorePreferenceFragment
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.core.ui.preference.setupAsEditTextPreference
import com.f0x1d.logfox.core.ui.preference.setupAsListPreference
import com.f0x1d.logfox.core.ui.view.setupBackButtonForNavController
import com.f0x1d.logfox.feature.preferences.presentation.R
import com.f0x1d.logfox.feature.preferences.presentation.fillWithStrings
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUICommand
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUISideEffect
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUIState
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUIViewModel
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUIViewState
import com.f0x1d.logfox.feature.strings.Strings
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class PreferencesUIFragment :
    BaseStorePreferenceFragment<
        PreferencesUIViewState,
        PreferencesUIState,
        PreferencesUICommand,
        PreferencesUISideEffect,
        PreferencesUIViewModel,
        >() {
    override val viewModel by viewModels<PreferencesUIViewModel>()

    private val filledThemeSettings by lazy {
        intArrayOf(
            Strings.follow_system,
            Strings.light,
            Strings.dark,
        ).fillWithStrings(requireContext())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_ui)

        findPreference<Preference>("pref_night_theme")?.apply {
            setupAsListPreference(
                setupDialog = { setIcon(Icons.ic_dialog_theme) },
                items = filledThemeSettings,
                selected = {
                    viewModel.state.value.nightTheme
                        .coerceAtLeast(0)
                },
                onSelected = { send(PreferencesUICommand.NightThemeChanged(it)) },
            )
        }

        findPreference<Preference>("pref_monet_enabled")?.apply {
            isVisible = monetAvailable
            setOnPreferenceChangeListener { _, _ ->
                send(PreferencesUICommand.MonetEnabledChanged)
                true
            }
        }

        findPreference<Preference>("pref_date_format")?.apply {
            setupAsEditTextPreference(
                setupViews = { it.textLayout.setHint(Strings.date_format) },
                setupDialog = { setIcon(Icons.ic_dialog_date_format) },
                get = { viewModel.state.value.dateFormat },
                save = { send(PreferencesUICommand.DateFormatChanged(it)) },
            )
        }

        findPreference<Preference>("pref_time_format")?.apply {
            setupAsEditTextPreference(
                setupViews = { it.textLayout.setHint(Strings.time_format) },
                setupDialog = { setIcon(Icons.ic_dialog_time_format) },
                get = { viewModel.state.value.timeFormat },
                save = { send(PreferencesUICommand.TimeFormatChanged(it)) },
            )
        }

        findPreference<Preference>("pref_logs_format")?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(Strings.logs_format)
                .setIcon(Icons.ic_dialog_list)
                .setMultiChoiceItems(
                    intArrayOf(
                        Strings.date,
                        Strings.time,
                        Strings.uid,
                        Strings.pid,
                        Strings.tid,
                        Strings.package_name,
                        Strings.tag,
                        Strings.content,
                    ).fillWithStrings(requireContext()),
                    viewModel.state.value.run {
                        booleanArrayOf(
                            showLogDate,
                            showLogTime,
                            showLogUid,
                            showLogPid,
                            showLogTid,
                            showLogPackage,
                            showLogTag,
                            showLogContent,
                        )
                    },
                ) { _, which, checked ->
                    send(PreferencesUICommand.LogsFormatChanged(which, checked))
                }.setPositiveButton(Strings.close, null)
                .show()
            true
        }

        findPreference<Preference>("pref_logs_update_interval")?.apply {
            setupAsEditTextPreference(
                setupViews = {
                    it.textLayout.setHint(Strings.in_ms)
                    it.text.inputType = InputType.TYPE_CLASS_NUMBER
                },
                setupDialog = { setIcon(Icons.ic_dialog_timer) },
                get = {
                    viewModel.state.value.logsUpdateInterval
                        .toString()
                },
                save = {
                    requireContext().catchingNotNumber {
                        send(PreferencesUICommand.LogsUpdateIntervalChanged(it?.toLong()))
                    }
                },
            )
        }

        findPreference<Preference>("pref_logs_text_size")?.apply {
            setupAsEditTextPreference(
                setupViews = { it.text.inputType = InputType.TYPE_CLASS_NUMBER },
                setupDialog = { setIcon(Icons.ic_dialog_text_fields) },
                get = {
                    viewModel.state.value.logsTextSize
                        .toString()
                },
                save = {
                    requireContext().catchingNotNumber {
                        send(PreferencesUICommand.LogsTextSizeChanged(it?.toInt()))
                    }
                },
            )
        }

        findPreference<Preference>("pref_logs_display_limit")?.apply {
            setupAsEditTextPreference(
                setupViews = {
                    it.textLayout.setHint(Strings.lines)
                    it.text.inputType = InputType.TYPE_CLASS_NUMBER
                },
                setupDialog = { setIcon(Icons.ic_dialog_eye) },
                get = {
                    viewModel.state.value.logsDisplayLimit
                        .toString()
                },
                save = {
                    requireContext().catchingNotNumber {
                        send(PreferencesUICommand.LogsDisplayLimitChanged(it?.toInt()))
                    }
                },
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.toolbar).apply {
            setTitle(Strings.ui)
            setupBackButtonForNavController()
        }

        listView.apply {
            clipToPadding = false
            applyInsetter {
                type(navigationBars = true) {
                    padding(vertical = requireContext().isHorizontalOrientation)
                }
            }
        }
    }

    override fun render(state: PreferencesUIViewState) {
        findPreference<Preference>("pref_night_theme")?.summary =
            filledThemeSettings.getOrNull(state.nightTheme) ?: getString(Strings.follow_system)
        findPreference<Preference>("pref_date_format")?.summary = state.dateFormat
        findPreference<Preference>("pref_time_format")?.summary = state.timeFormat
        findPreference<Preference>("pref_logs_update_interval")?.summary =
            state.logsUpdateInterval.toString()
        findPreference<Preference>("pref_logs_text_size")?.summary = state.logsTextSize.toString()
        findPreference<Preference>("pref_logs_display_limit")?.summary =
            state.logsDisplayLimit.toString()
    }

    override fun handleSideEffect(sideEffect: PreferencesUISideEffect) {
        when (sideEffect) {
            is PreferencesUISideEffect.RecreateActivity -> {
                requireActivity().recreate()
            }

            // Business logic side effects - handled by EffectHandler
            else -> {
                Unit
            }
        }
    }
}
