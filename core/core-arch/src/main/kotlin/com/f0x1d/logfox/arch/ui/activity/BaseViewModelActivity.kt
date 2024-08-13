package com.f0x1d.logfox.arch.ui.activity

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.arch.viewmodel.Event
import com.f0x1d.logfox.arch.viewmodel.ShowSnackbar

abstract class BaseViewModelActivity<T : BaseViewModel, D : ViewBinding>: BaseActivity<D>() {

    abstract val viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.eventsFlow.collectWithLifecycle(collector = ::onEvent)
    }

    open fun onEvent(event: Event) {
        when (event) {
            is ShowSnackbar -> snackbar(event.text)
        }
    }
}
