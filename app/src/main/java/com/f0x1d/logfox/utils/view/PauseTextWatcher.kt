package com.f0x1d.logfox.utils.view

import android.text.Editable
import android.text.TextWatcher

class PauseTextWatcher(private val listener: (Editable?) -> Unit): TextWatcher {

    var paused = false

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(editable: Editable?) {
        if (!paused) listener.invoke(editable)
    }

    fun paused(block: () -> Unit) {
        paused = true
        block.invoke()
        paused = false
    }
}