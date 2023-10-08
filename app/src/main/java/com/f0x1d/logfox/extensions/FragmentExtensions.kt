package com.f0x1d.logfox.extensions

import androidx.fragment.app.Fragment
import com.f0x1d.logfox.R

@Deprecated("Avoid apologies (Sorry for the interruption), extra alarm (Warning!), or ambiguity (Are you sure?)",
    ReplaceWith("showAreYouSureDialog(title,message,okPressed)")
)
fun Fragment.showAreYouSureDialog(okPressed: () -> Unit) =
    requireActivity().showAreYouSureDialog(okPressed)

fun Fragment.showAreYouSureDialog(
    title: Int,
    message: Int = R.string.are_you_sure, okPressed: () -> Unit
) = requireActivity().showAreYouSureDialog(title, message, okPressed)