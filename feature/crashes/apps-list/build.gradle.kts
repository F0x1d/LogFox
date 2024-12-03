plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.crashes.apps.list"

dependencies {
    implementation(projects.feature.crashes.api)
    implementation(projects.feature.crashes.common)
}
