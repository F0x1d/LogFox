package com.f0x1d.logfox.extensions

import androidx.fragment.app.FragmentActivity
import com.f0x1d.logfox.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun FragmentActivity.showAreYouSureDialog(okClicked: () -> Unit) {
    MaterialAlertDialogBuilder(this)
        .setIcon(R.drawable.ic_dialog_warning)
        .setTitle(R.string.are_you_sure)
        .setPositiveButton(R.string.yes) { dialog, i -> okClicked() }
        .setNeutralButton(R.string.no, null)
        .show()
}

fun FragmentActivity.showAreYouSureDialog(title: Int, message: Int = R.string.are_you_sure, okClicked: () -> Unit) {
    MaterialAlertDialogBuilder(this)
        .setIcon(R.drawable.ic_dialog_warning)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(R.string.yes) { dialog, i -> okClicked() }
        .setNeutralButton(R.string.no, null)
        .show()
}