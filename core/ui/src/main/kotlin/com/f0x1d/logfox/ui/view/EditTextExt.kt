package com.f0x1d.logfox.ui.view

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class ExtendedTextWatcher(
    val editText: EditText,
    var enabled: Boolean = true,
    private val doAfterTextChanged: (e: Editable?) -> Unit,
) : TextWatcher {

    init {
        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

    override fun afterTextChanged(e: Editable?) {
        if (enabled) {
            doAfterTextChanged(e)
        }
    }

    fun setText(text: String?) {
        enabled = false
        editText.setText(text)
        enabled = true
    }
}

fun EditText.applyExtendedTextWatcher(doAfterTextChanged: (e: Editable?) -> Unit): ExtendedTextWatcher = ExtendedTextWatcher(
    editText = this,
    doAfterTextChanged = doAfterTextChanged,
)
