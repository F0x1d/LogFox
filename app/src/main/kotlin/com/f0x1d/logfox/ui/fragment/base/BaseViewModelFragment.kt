package com.f0x1d.logfox.ui.fragment.base

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.model.event.Event

abstract class BaseViewModelFragment<T : BaseViewModel, D : ViewBinding>: BaseFragment<D>() {

    abstract val viewModel: T

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.eventsData.observe(viewLifecycleOwner) {
            if (it.isConsumed) return@observe

            onEvent(it)
        }

        viewModel.snackbarEventsData.observe(viewLifecycleOwner) {
            if (it.isConsumed) return@observe

            it.consume<String>()?.also { message ->
                snackbar(message)
            }
        }
    }

    open fun onEvent(event: Event) = Unit
}
