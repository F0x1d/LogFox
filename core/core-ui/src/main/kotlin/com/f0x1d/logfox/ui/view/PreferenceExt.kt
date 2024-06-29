package com.f0x1d.logfox.ui.view

import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import com.f0x1d.logfox.context.inputMethodManager
import com.f0x1d.logfox.preferences.shared.appPreferences
import com.f0x1d.logfox.strings.Strings
import com.f0x1d.logfox.ui.databinding.DialogTextBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Preference.setupAsEditTextPreference(
    setupViews: (DialogTextBinding) -> Unit,
    setupDialog: MaterialAlertDialogBuilder.() -> Unit,
    get: () -> String?,
    save: (String?) -> Unit
) = setOnPreferenceClickListener {
    val dialogBinding = DialogTextBinding.inflate(LayoutInflater.from(context))
    setupViews(dialogBinding)

    dialogBinding.text.setText(get())

    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setView(dialogBinding.root)
        .setPositiveButton(android.R.string.ok) { _, _ ->
            save(dialogBinding.text.text?.toString())
        }
        .setNegativeButton(Strings.close, null)
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

fun Preference.setupAsListPreference(
    setupDialog: MaterialAlertDialogBuilder.() -> Unit,
    items: Array<String>,
    selected: () -> Int,
    onSelected: (Int) -> Unit
) = setOnPreferenceClickListener {
    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setSingleChoiceItems(items, selected()) { dialog, which ->
            dialog.cancel()
            onSelected(which)
        }
        .setPositiveButton(Strings.close, null)
        .apply(setupDialog)
        .show()
    return@setOnPreferenceClickListener true
}

fun Preference.observeAndUpdateSummaryForList(
    observer: LifecycleOwner,
    defValue: Int,
    items: Array<String>
) = observeAndUpdateSummary(observer, defValue) {
    summary = items[it]
}

inline fun <reified T> Preference.observeAndUpdateSummary(
    observer: LifecycleOwner,
    defValue: T
) {
    observeAndUpdateSummary(observer, defValue) {
        summary = it.toString()
    }
}

inline fun <reified T> Preference.observeAndUpdateSummary(
    observer: LifecycleOwner,
    defValue: T,
    crossinline block: (T) -> Unit
) {
    context.appPreferences.asLiveData(key, defValue).observe(observer) {
        block(it ?: return@observe)
    }
}
