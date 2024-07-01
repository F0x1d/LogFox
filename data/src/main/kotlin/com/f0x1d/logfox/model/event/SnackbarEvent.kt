package com.f0x1d.logfox.model.event

class SnackbarEvent(text: String): Event(TYPE, text) {
    companion object {
        const val TYPE = "snackbar"
    }
}
