plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.recordings.core"

dependencies {
    implementation(projects.feature.featureLoggingCore)
}
