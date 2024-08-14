plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.logging"

dependencies {
    implementation(projects.feature.featureCrashesCore)
    implementation(projects.feature.featureFiltersCore)
    implementation(projects.feature.featureLoggingCore)
    implementation(projects.feature.featureRecordingsCore)
}
