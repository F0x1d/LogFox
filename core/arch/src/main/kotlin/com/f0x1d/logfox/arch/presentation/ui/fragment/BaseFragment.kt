package com.f0x1d.logfox.arch.presentation.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.f0x1d.logfox.arch.presentation.ui.base.SimpleFragmentLifecycleOwner
import com.f0x1d.logfox.arch.presentation.ui.snackbar
import dev.chrisbanes.insetter.applyInsetter

abstract class BaseFragment<T : ViewBinding> : Fragment(), SimpleFragmentLifecycleOwner {

    private var mutableBinding: T? = null
    protected val binding: T get() = mutableBinding!!

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): T?
    protected open fun T.onViewCreated(view: View, savedInstanceState: Bundle?) = Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflateBinding(inflater, container)?.also {
            mutableBinding = it
            return it.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }

    protected open fun snackbar(text: String) = requireView().snackbar(text).apply {
        view.applyInsetter {
            type(navigationBars = true) {
                margin(vertical = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            }
        }
    }

    protected fun snackbar(id: Int) = snackbar(getString(id))
}
