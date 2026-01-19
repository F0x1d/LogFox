package com.f0x1d.logfox.feature.preferences.presentation.crashes.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.core.context.isHorizontalOrientation
import com.f0x1d.logfox.core.presentation.view.setupBackButtonForNavController
import com.f0x1d.logfox.core.tea.BaseStorePreferenceFragment
import com.f0x1d.logfox.feature.preferences.presentation.R
import com.f0x1d.logfox.feature.preferences.presentation.crashes.PreferencesCrashesCommand
import com.f0x1d.logfox.feature.preferences.presentation.crashes.PreferencesCrashesSideEffect
import com.f0x1d.logfox.feature.preferences.presentation.crashes.PreferencesCrashesState
import com.f0x1d.logfox.feature.preferences.presentation.crashes.PreferencesCrashesViewModel
import com.f0x1d.logfox.feature.strings.Strings
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class PreferencesCrashesFragment :
    BaseStorePreferenceFragment<
        PreferencesCrashesState,
        PreferencesCrashesCommand,
        PreferencesCrashesSideEffect,
        PreferencesCrashesViewModel,
        >() {

    override val viewModel by viewModels<PreferencesCrashesViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_crashes)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.toolbar).apply {
            setTitle(Strings.crashes)
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

    override fun render(state: PreferencesCrashesState) = Unit

    override fun handleSideEffect(sideEffect: PreferencesCrashesSideEffect) = Unit
}
