package com.f0x1d.logfox.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

val Number.toPx get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)

fun IntArray.fillWithStrings(ctx: Context) = map { ctx.getString(it) }.toTypedArray()

fun <T : ViewModel> viewModelFactory(block: () -> T) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) = block.invoke() as T
}