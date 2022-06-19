package com.f0x1d.logfox.utils

import android.app.PendingIntent
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
else
    PendingIntent.FLAG_UPDATE_CURRENT

val Number.toPx get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)

fun IntArray.fillWithStrings(ctx: Context) = map { ctx.getString(it) }.toTypedArray()

inline fun <T : ViewModel> viewModelFactory(crossinline block: () -> T) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) = block.invoke() as T
}

fun Int.toDrawable(ctx: Context) = ctx.getDrawable(this)
fun Int.toString(ctx: Context) = ctx.getString(this)