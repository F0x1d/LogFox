plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.logging.extended.copy"

dependencies {
    implementation(projects.feature.logging.api)
}
