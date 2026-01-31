package com.f0x1d.logfox.feature.logging.presentation.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.core.tea.BaseStoreBottomSheetFragment
import com.f0x1d.logfox.feature.logging.presentation.databinding.SheetSearchBinding
import com.f0x1d.logfox.feature.logging.presentation.search.SearchLogsCommand
import com.f0x1d.logfox.feature.logging.presentation.search.SearchLogsSideEffect
import com.f0x1d.logfox.feature.logging.presentation.search.SearchLogsState
import com.f0x1d.logfox.feature.logging.presentation.search.SearchLogsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class SearchLogsBottomSheetFragment :
    BaseStoreBottomSheetFragment<
        SheetSearchBinding,
        SearchLogsState,
        SearchLogsState,
        SearchLogsCommand,
        SearchLogsSideEffect,
        SearchLogsViewModel,
        >() {

    override val viewModel by viewModels<SearchLogsViewModel>()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = SheetSearchBinding.inflate(inflater, container, false)

    override fun SheetSearchBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        clearSearchButton.setOnClickListener {
            search(null)
        }

        searchButton.setOnClickListener {
            search(queryText.text?.toString())
        }
        queryText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(queryText.text?.toString())
                true
            } else {
                false
            }
        }

        caseSensitiveCheckbox.setOnClickListener {
            send(SearchLogsCommand.ToggleCaseSensitive)
        }

        queryText.requestFocus()
    }

    override fun render(state: SearchLogsState) {
        binding.queryText.setText(state.query)
        binding.clearSearchButton.isVisible = state.query != null
        binding.caseSensitiveCheckbox.isChecked = state.caseSensitive
    }

    override fun handleSideEffect(sideEffect: SearchLogsSideEffect) {
        when (sideEffect) {
            is SearchLogsSideEffect.Dismiss -> dismiss()

            // Business logic side effects - handled by EffectHandler
            else -> Unit
        }
    }

    private fun search(text: String?) {
        if (text?.isEmpty() == true) return

        send(SearchLogsCommand.UpdateQuery(text))
    }
}
