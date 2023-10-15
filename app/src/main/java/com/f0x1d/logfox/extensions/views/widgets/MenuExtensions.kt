package com.f0x1d.logfox.extensions.views.widgets

import android.view.Menu
import android.view.MenuItem

fun Menu.setClickListenerOn(id: Int, block: (MenuItem) -> Unit) = findItem(id).setOnMenuItemClickListener {
    block(it)
    return@setOnMenuItemClickListener true
}