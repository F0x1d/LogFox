package com.f0x1d.logfox.utils

class OneTimeAction {

    private var done = false

    fun doIfNotDone(action: () -> Unit) {
        if (!done) {
            action.invoke()
            done = true
        }
    }
}