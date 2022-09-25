package com.f0x1d.logfox.extensions

import android.view.LayoutInflater
import android.view.WindowManager
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import com.f0x1d.logfox.databinding.DialogTextBinding
import com.f0x1d.logfox.utils.preferences.AppPreferences
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
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .apply {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                dialogBinding.text.requestFocus()
            }
            .show()
        return@setOnPreferenceClickListener true
    }
}

fun Preference.setupAsListPreference(items: Array<String>, selected: Int, onSelected: (Int) -> Unit) {
    setOnPreferenceClickListener {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setSingleChoiceItems(items, selected) { dialog, which ->
                dialog.cancel()
                onSelected.invoke(which)
            }
            .setPositiveButton(android.R.string.cancel, null)
            .show()
        return@setOnPreferenceClickListener true
    }
}

fun Preference.observeAndUpdateSummaryForList(appPreferences: AppPreferences, observer: LifecycleOwner, defValue: Int, list: Array<String>) {
    observeAndUpdateSummary(appPreferences, observer, defValue) {
        summary = list[it]
    }
}

inline fun <reified T> Preference.observeAndUpdateSummary(appPreferences: AppPreferences, observer: LifecycleOwner, defValue: T) {
    observeAndUpdateSummary(appPreferences, observer, defValue) {
        summary = it.toString()
    }
}

inline fun <reified T> Preference.observeAndUpdateSummary(appPreferences: AppPreferences, observer: LifecycleOwner, defValue: T, crossinline block: (T) -> Unit) {
    appPreferences.asLiveData(key, defValue).observe(observer) {
        block.invoke(it)
    }
}