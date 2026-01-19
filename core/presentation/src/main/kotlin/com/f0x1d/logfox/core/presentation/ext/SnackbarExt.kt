package com.f0x1d.logfox.core.presentation.ext

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(text: String) = Snackbar.make(this, text, Snackbar.LENGTH_SHORT).apply { show() }
