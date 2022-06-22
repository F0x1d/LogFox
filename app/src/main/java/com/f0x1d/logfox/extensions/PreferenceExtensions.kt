package com.f0x1d.logfox.extensions

import android.view.LayoutInflater
import android.view.WindowManager
import androidx.preference.Preference
import com.f0x1d.logfox.databinding.DialogTextBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Preference.setupAsEditTextPreference(setup: (DialogTextBinding) -> Unit, get: () -> String?, save: (String?) -> Unit) {
    setOnPreferenceClickListener {
        val dialogBinding = DialogTextBinding.inflate(LayoutInflater.from(context))
        setup.invoke(dialogBinding)

        dialogBinding.text.setText(get.invoke())

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                save.invoke(dialogBinding.text.text?.toString())
            }
            .setNeutralButton(android.R.string.cancel, null)
            .create()
            .apply {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                dialogBinding.text.requestFocus()
            }
            .show()
        return@setOnPreferenceClickListener true
    }
}