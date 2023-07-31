package com.f0x1d.logfox.ui.dialog.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet<T : ViewBinding>: BottomSheetDialogFragment() {

    private var mutableBinding: T? = null
    protected val binding: T get() = mutableBinding!!

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): T?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflateBinding(inflater, container)?.also {
            mutableBinding = it
            return it.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.skipCollapsed = true
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.disableShapeAnimations() // i love google https://github.com/material-components/material-components-android/pull/437
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mutableBinding = null
    }
}