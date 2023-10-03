package com.f0x1d.logfox.ui.activity.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.utils.event.Event
import com.f0x1d.logfox.viewmodel.base.BaseViewModel

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

    open fun onEvent(event: Event) {}
}