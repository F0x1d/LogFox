package com.f0x1d.logfox.core.presentation.view

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.f0x1d.logfox.core.presentation.Icons
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
