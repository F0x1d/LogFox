package com.f0x1d.logfox.extensions.views.widgets

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.f0x1d.logfox.R

fun Toolbar.setupBackButton(onClickListener: View.OnClickListener) {
    setNavigationIcon(R.drawable.ic_arrow_back)
    setNavigationOnClickListener(onClickListener)
    setNavigationContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description)
}

fun Toolbar.setupBackButtonForNavController() = setupBackButton {
    findNavController().popBackStack()
}

fun Toolbar.setupCloseButton() {
    setNavigationIcon(R.drawable.ic_clear)
    setNavigationContentDescription(R.string.close)
}

fun Toolbar.invalidateNavigationButton() {
    navigationIcon = null
    navigationContentDescription = null
}