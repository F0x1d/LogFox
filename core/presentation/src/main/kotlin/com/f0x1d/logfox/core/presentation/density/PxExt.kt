package com.f0x1d.logfox.core.presentation.density

import android.content.res.Resources
import android.util.TypedValue

val Number.dpToPx get() = toPx(TypedValue.COMPLEX_UNIT_DIP)
fun Number.toPx(what: Int) = TypedValue.applyDimension(what, this.toFloat(), Resources.getSystem().displayMetrics)
