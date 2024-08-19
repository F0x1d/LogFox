package com.f0x1d.logfox.arch.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.arch.viewmodel.Event
import com.f0x1d.logfox.arch.viewmodel.ShowSnackbar

abstract class BaseViewModelFragment<T : BaseViewModel, D : ViewBinding>: BaseFragment<D>() {

    abstract val viewModel: T

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.eventsFlow.collectWithLifecycle(collector = ::onEvent)
    }

    open fun onEvent(event: Event) {
        when (event) {
            is ShowSnackbar -> snackbar(event.text)
        }
    }
}
