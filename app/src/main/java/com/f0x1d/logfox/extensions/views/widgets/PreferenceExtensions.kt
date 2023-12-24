package com.f0x1d.logfox.extensions.views.widgets

import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import com.f0x1d.logfox.R
import com.f0x1d.logfox.databinding.DialogTextBinding
import com.f0x1d.logfox.extensions.context.inputMethodManager
import com.f0x1d.logfox.utils.preferences.AppPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Preference.setupAsEditTextPreference(setup: (DialogTextBinding) -> Unit, setupDialog: MaterialAlertDialogBuilder.() -> Unit, get: () -> String?, save: (String?) -> Unit) {
    setOnPreferenceClickListener {
        val dialogBinding = DialogTextBinding.inflate(LayoutInflater.from(context))
        setup(dialogBinding)

        dialogBinding.text.setText(get())

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                save(dialogBinding.text.text?.toString())
            }
            .setNegativeButton(R.string.close, null)
            .apply(setupDialog)
            .create()
            .apply {
                setOnShowListener {
                    dialogBinding.text.apply {
                        requestFocus()
                        setSelection(text?.length ?: 0)

                        postDelayed({
                            context.inputMethodManager.showSoftInput(
                                this,
                                InputMethodManager.SHOW_IMPLICIT
                            )
                        }, 100)
                    }
                }
            }
            .show()
        return@setOnPreferenceClickListener true
    }
}

fun Preference.setupAsListPreference(setupDialog: MaterialAlertDialogBuilder.() -> Unit, items: Array<String>, selected: () -> Int, onSelected: (Int) -> Unit) {
    setOnPreferenceClickListener {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setSingleChoiceItems(items, selected()) { dialog, which ->
                dialog.cancel()
                onSelected(which)
            }
            .setPositiveButton(R.string.close, null)
            .apply(setupDialog)
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
        block(it ?: return@observe)
    }
}