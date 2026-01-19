package com.f0x1d.logfox.core.presentation.dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.f0x1d.logfox.core.presentation.Icons
import com.f0x1d.logfox.feature.strings.Strings
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun FragmentActivity.showAreYouSureDialog(
    title: Int,
    message: Int = Strings.are_you_sure,
    okClicked: () -> Unit,
) = MaterialAlertDialogBuilder(this)
    .setIcon(Icons.ic_dialog_warning)
    .setTitle(title)
    .setMessage(message)
    .setPositiveButton(Strings.yes) { _, _ -> okClicked() }
    .setNeutralButton(Strings.no, null)
    .show()

fun FragmentActivity.showAreYouSureDeleteDialog(okClicked: () -> Unit) = showAreYouSureDialog(
    title = Strings.delete,
    message = Strings.delete_warning,
    okClicked = okClicked,
)
fun FragmentActivity.showAreYouSureClearDialog(okClicked: () -> Unit) = showAreYouSureDialog(
    title = Strings.clear,
    message = Strings.clear_warning,
    okClicked = okClicked,
)

fun Fragment.showAreYouSureDialog(
    title: Int,
    message: Int = Strings.are_you_sure,
    okClicked: () -> Unit,
) = requireActivity().showAreYouSureDialog(
    title = title,
    message = message,
    okClicked = okClicked,
)

fun Fragment.showAreYouSureDeleteDialog(okClicked: () -> Unit) = requireActivity().showAreYouSureDeleteDialog(okClicked)
fun Fragment.showAreYouSureClearDialog(okClicked: () -> Unit) = requireActivity().showAreYouSureClearDialog(okClicked)
