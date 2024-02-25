package com.f0x1d.logfox.ui.dialog.base

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.base.BaseViewModel

abstract class BaseViewModelBottomSheet<T : BaseViewModel, D : ViewBinding>: BaseBottomSheet<D>() {

    abstract val viewModel: T

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.eventsData.observe(viewLifecycleOwner) {
            if (it.isConsumed) return@observe

            onEvent(it)
        }
    }

    open fun onEvent(event: Event) = Unit
}