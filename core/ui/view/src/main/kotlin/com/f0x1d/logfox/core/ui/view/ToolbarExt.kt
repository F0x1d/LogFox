package com.f0x1d.logfox.core.ui.view

import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.navigation.findNavController
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.core.ui.view.density.dpToPx
import com.f0x1d.logfox.feature.strings.Strings

fun Toolbar.setupBackButton(onClickListener: View.OnClickListener) {
    setNavigationIcon(Icons.ic_arrow_back)
    setNavigationOnClickListener(onClickListener)
    setNavigationContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description)
}

fun Toolbar.setupBackButtonForNavController() = setupBackButton {
    findNavController().popBackStack()
}

fun Toolbar.setupCloseButton() {
    setNavigationIcon(Icons.ic_clear)
    setNavigationContentDescription(Strings.close)
}

fun Toolbar.invalidateNavigationButton() {
    navigationIcon = null
    navigationContentDescription = null
}

/**
 * Make the toolbar title behave like a tappable, focusable element that opens
 * an edit affordance — Google Docs-style document title in the AppBar.
 *
 * Wires once; safe to call before [Toolbar.setTitle] (the title TextView is
 * created lazily on first title set, so this defers to `doOnLayout`).
 */
fun Toolbar.setupClickableTitle(
    @DrawableRes background: Int,
    onClick: () -> Unit,
) = doOnLayout {
    val titleView = titleTextView() ?: return@doOnLayout
    titleView.background = ContextCompat.getDrawable(context, background)
    val padHorizontal = 8.dpToPx.toInt()
    val padVertical = 4.dpToPx.toInt()
    titleView.setPaddingRelative(padHorizontal, padVertical, padHorizontal, padVertical)
    titleView.isClickable = true
    titleView.isFocusable = true
    titleView.setOnClickListener { onClick() }
}

private fun Toolbar.titleTextView(): TextView? = (0 until childCount)
    .map { getChildAt(it) }
    .firstOrNull { it is TextView } as? TextView
