plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.logging"

dependencies {
    implementation(projects.feature.crashesCore)
    implementation(projects.feature.filtersCore)
    implementation(projects.feature.loggingCore)
    implementation(projects.feature.recordingsCore)
}
