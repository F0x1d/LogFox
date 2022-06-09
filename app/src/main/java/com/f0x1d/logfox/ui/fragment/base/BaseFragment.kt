package com.f0x1d.logfox.ui.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding>: Fragment() {

    private var mutableBinding: T? = null
    protected val binding: T
        get() = mutableBinding!!

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): T?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflateBinding(inflater, container)?.also {
            mutableBinding = it
            return it.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}