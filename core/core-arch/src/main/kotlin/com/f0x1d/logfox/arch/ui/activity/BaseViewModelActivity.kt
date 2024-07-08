package com.f0x1d.logfox.arch.ui.activity

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.model.event.Event

abstract class BaseViewModelActivity<T : BaseViewModel, D : ViewBinding>: BaseActivity<D>() {

    abstract val viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.eventsData.observe(this) {
            if (it.isConsumed) return@observe

            onEvent(it)
        }

        viewModel.snackbarEventsData.observe(this) {
            if (it.isConsumed) return@observe

            it.consume<String>()?.also { message ->
                snackbar(message)
            }
        }
    }

    open fun onEvent(event: Event) = Unit
}
