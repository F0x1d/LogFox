package com.f0x1d.logfox.core.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import androidx.appcompat.widget.AppCompatTextView

class NoDragTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    // https://github.com/F0x1d/LogFox/issues/216
    override fun onDragEvent(event: DragEvent?): Boolean = false

    override fun performLongClick(): Boolean = try {
        super.performLongClick()
    } catch (_: IllegalStateException) {
        // Drag shadow dimensions must be positive
        true
    }
}
