package com.f0x1d.logfox.feature.logging.search.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.f0x1d.logfox.arch.presentation.ui.dialog.BaseBottomSheetFragment
import com.f0x1d.logfox.feature.logging.search.databinding.SheetSearchBinding
import com.f0x1d.logfox.feature.logging.search.presentation.SearchLogsAction
import com.f0x1d.logfox.feature.logging.search.presentation.SearchLogsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchLogsBottomSheetFragment : BaseBottomSheetFragment<SheetSearchBinding>() {

    private val viewModel by viewModels<SearchLogsViewModel>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = SheetSearchBinding.inflate(inflater, container, false)

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
            } else
                false
        }

        queryText.requestFocus()

        viewModel.state.collectWithLifecycle { state ->
            queryText.setText(state.query)

            clearSearchButton.isVisible = state.query != null
        }

        viewModel.actions.collectWithLifecycle { action ->
            when (action) {
                is SearchLogsAction.Dismiss -> dismiss()
            }
        }
    }

    private fun search(text: String?) {
        if (text?.isEmpty() == true) return

        viewModel.updateQuery(text)
    }
}
