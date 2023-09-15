package com.f0x1d.logfox.extensions

import androidx.fragment.app.Fragment

fun Fragment.showAreYouSureDialog(okPressed: () -> Unit) = requireActivity().showAreYouSureDialog(okPressed)