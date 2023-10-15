package com.f0x1d.logfox.extensions.widgets

import android.view.Menu
import android.view.MenuItem

fun Menu.setClickListenerOn(id: Int, block: (MenuItem) -> Unit) = findItem(id).setOnMenuItemClickListener {
    block.invoke(it)
    return@setOnMenuItemClickListener true
}