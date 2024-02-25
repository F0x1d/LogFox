package com.f0x1d.logfox.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.SheetSearchBinding
import com.f0x1d.logfox.ui.dialog.base.BaseBottomSheet
import com.f0x1d.logfox.viewmodel.LogsViewModel

class SearchBottomSheet: BaseBottomSheet<SheetSearchBinding>() {

    private val logsViewModel by hiltNavGraphViewModels<LogsViewModel>(R.id.logsFragment)

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = SheetSearchBinding.inflate(inflater, container, false)

    override fun SheetSearchBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        val query = logsViewModel.query.value

        queryText.setText(query)

        clearSearchButton.isVisible = query != null
        clearSearchButton.setOnClickListener {
            search(null)
        }

        searchButton.setOnClickListener {
            search(queryText.text?.toString())
        }
        queryText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(queryText.text?.toString())
                true
            } else
                false
        }

        queryText.requestFocus()
    }

    private fun search(text: String?) {
        if (text?.isEmpty() == true) return

        logsViewModel.query(text)
        dismiss()
    }
}