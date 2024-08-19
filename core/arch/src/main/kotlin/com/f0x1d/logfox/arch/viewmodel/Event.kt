package com.f0x1d.logfox.arch.viewmodel

interface Event

data class ShowSnackbar(
    val text: String,
) : Event
