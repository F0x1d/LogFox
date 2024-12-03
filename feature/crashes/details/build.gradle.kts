plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.crashes.details"

dependencies {
    implementation(projects.feature.crashes.api)
    implementation(projects.feature.crashes.common)
}
