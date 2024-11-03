plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.crashes.impl"

dependencies {
    implementation(projects.feature.crashes.api)
}
