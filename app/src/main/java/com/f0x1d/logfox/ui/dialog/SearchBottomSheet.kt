package com.f0x1d.logfox.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.navArgs
import com.f0x1d.logfox.databinding.SheetSearchBinding
import com.f0x1d.logfox.ui.dialog.base.BaseBottomSheet

class SearchBottomSheet: BaseBottomSheet<SheetSearchBinding>() {

    private val navArgs by navArgs<SearchBottomSheetArgs>()
    private val searchClicked by lazy {
        parentFragment as OnSearchClicked
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = SheetSearchBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.queryText.setText(navArgs.query)

        binding.clearSearchButton.visibility = if (navArgs.query == null) View.GONE else View.VISIBLE
        binding.clearSearchButton.setOnClickListener {
            searchClicked.searchClicked(null)
            dismiss()
        }

        binding.searchButton.setOnClickListener {
            search(binding.queryText.text?.toString())
        }
        binding.queryText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(binding.queryText.text?.toString())
                true
            } else
                false
        }

        binding.queryText.requestFocus()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).apply {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun search(text: String?) {
        if (text == null) return
        if (text.isEmpty()) return

        searchClicked.searchClicked(text)
        dismiss()
    }

    interface OnSearchClicked {
        fun searchClicked(query: String?)
    }
}