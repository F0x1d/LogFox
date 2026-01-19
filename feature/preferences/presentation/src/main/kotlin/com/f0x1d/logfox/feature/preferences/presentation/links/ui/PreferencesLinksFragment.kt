package com.f0x1d.logfox.feature.preferences.presentation.links.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.core.context.isHorizontalOrientation
import com.f0x1d.logfox.core.presentation.view.setupBackButtonForNavController
import com.f0x1d.logfox.core.tea.BaseStorePreferenceFragment
import com.f0x1d.logfox.feature.preferences.presentation.R
import com.f0x1d.logfox.feature.preferences.presentation.links.PreferencesLinksCommand
import com.f0x1d.logfox.feature.preferences.presentation.links.PreferencesLinksSideEffect
import com.f0x1d.logfox.feature.preferences.presentation.links.PreferencesLinksState
import com.f0x1d.logfox.feature.preferences.presentation.links.PreferencesLinksViewModel
import com.f0x1d.logfox.feature.strings.Strings
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
internal class PreferencesLinksFragment :
    BaseStorePreferenceFragment<
        PreferencesLinksState,
        PreferencesLinksCommand,
        PreferencesLinksSideEffect,
        PreferencesLinksViewModel,
        >() {

    override val viewModel by viewModels<PreferencesLinksViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_links)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialToolbar>(R.id.toolbar).apply {
            setTitle(Strings.links)
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

    override fun render(state: PreferencesLinksState) = Unit

    override fun handleSideEffect(sideEffect: PreferencesLinksSideEffect) = Unit
}
