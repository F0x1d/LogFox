package com.f0x1d.logfox.feature.preferences.presentation

import android.content.Context

fun IntArray.fillWithStrings(ctx: Context) = map { ctx.getString(it) }.toTypedArray()
