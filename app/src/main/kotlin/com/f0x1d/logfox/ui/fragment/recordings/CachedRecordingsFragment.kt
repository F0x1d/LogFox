package com.f0x1d.logfox.ui.fragment.recordings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.f0x1d.logfox.R
import com.f0x1d.logfox.adapter.RecordingsAdapter
import com.f0x1d.logfox.database.entity.LogRecording
import com.f0x1d.logfox.databinding.FragmentCachedRecordingsBinding
import com.f0x1d.logfox.extensions.context.isHorizontalOrientation
import com.f0x1d.logfox.extensions.dpToPx
import com.f0x1d.logfox.extensions.showAreYouSureClearDialog
import com.f0x1d.logfox.extensions.showAreYouSureDeleteDialog
import com.f0x1d.logfox.extensions.views.widgets.setClickListenerOn
import com.f0x1d.logfox.extensions.views.widgets.setupBackButtonForNavController
import com.f0x1d.logfox.ui.fragment.base.BaseViewModelFragment
import com.f0x1d.logfox.viewmodel.recordings.RecordingsViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class CachedRecordingsFragment: BaseViewModelFragment<RecordingsViewModel, FragmentCachedRecordingsBinding>() {

    override val viewModel by viewModels<RecordingsViewModel>()

    private val adapter = RecordingsAdapter(
        click = {
            openDetails(it)
        },
        delete = {
            showAreYouSureDeleteDialog {
                viewModel.delete(it)
            }
        }
    )

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCachedRecordingsBinding.inflate(inflater, container, false)

    override fun FragmentCachedRecordingsBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireContext().isHorizontalOrientation.also { horizontalOrientation ->
            recordingsRecycler.applyInsetter {
                type(navigationBars = true) {
                    padding(vertical = horizontalOrientation)
                }
            }
        }

        toolbar.setupBackButtonForNavController()
        toolbar.menu.apply {
            setClickListenerOn(R.id.clear_item) {
                showAreYouSureClearDialog {
                    viewModel.clearCachedRecordings()
                }
            }
        }

        recordingsRecycler.layoutManager = LinearLayoutManager(requireContext())
        recordingsRecycler.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            ).apply {
                dividerInsetStart = 80.dpToPx.toInt()
                dividerInsetEnd = 10.dpToPx.toInt()
                isLastItemDecorated = false
            })
        recordingsRecycler.adapter = adapter

        viewModel.cachedRecordings.observe(viewLifecycleOwner) {
            placeholderLayout.root.isVisible = it.isEmpty()

            adapter.submitList(it)
        }
    }

    private fun openDetails(recording: LogRecording?) = recording?.id?.also {
        findNavController().navigate(
            RecordingsFragmentDirections.actionRecordingsFragmentToRecordingBottomSheet(it)
        )
    }
}