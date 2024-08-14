package com.f0x1d.logfox.ui.view

import android.widget.CompoundButton

class OnlyUserCheckedChangeListener(
    private val buttonView: CompoundButton,
    private val listener: CompoundButton.OnCheckedChangeListener
): CompoundButton.OnCheckedChangeListener {

    var enabled = true

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (enabled) listener.onCheckedChanged(buttonView, isChecked)
    }

    fun check(isChecked: Boolean) {
        enabled = false
        buttonView.isChecked = isChecked
        enabled = true
    }
}
