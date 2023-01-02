package com.f0x1d.logfox.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

val Number.dpToPx get() = toPx(TypedValue.COMPLEX_UNIT_DIP)
fun Number.toPx(what: Int) = TypedValue.applyDimension(what, this.toFloat(), Resources.getSystem().displayMetrics)

fun IntArray.fillWithStrings(ctx: Context) = map { ctx.getString(it) }.toTypedArray()

fun Int.toString(ctx: Context) = ctx.getString(this)