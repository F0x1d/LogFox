package com.f0x1d.logfox.utils.event

class SnackbarEvent(text: String): Event(TYPE, text) {
    companion object {
        const val TYPE = "snackbar"
    }
}