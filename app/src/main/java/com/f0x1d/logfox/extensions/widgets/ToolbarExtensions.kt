package com.f0x1d.logfox.extensions.widgets

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.f0x1d.logfox.R

fun Toolbar.setupBackButton(onClickListener: View.OnClickListener) {
    navigationIcon ?: setNavigationIcon(R.drawable.ic_arrow_back)
    setNavigationOnClickListener(onClickListener)
    setNavigationContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description)
}

fun Toolbar.setupBackButtonForNavController() {
    setupBackButton { findNavController().popBackStack() }
}