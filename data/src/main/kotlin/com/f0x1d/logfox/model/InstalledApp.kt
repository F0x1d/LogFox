package com.f0x1d.logfox.model

data class InstalledApp(
    val title: CharSequence,
    val packageName: String,
) : Identifiable {
    override val id: Any get() = packageName
}
