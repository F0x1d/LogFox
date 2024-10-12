package com.f0x1d.logfox.model

data class InstalledApp(
    val title: String,
    val packageName: String,
) : Identifiable {
    override val id: Any get() = packageName
}
