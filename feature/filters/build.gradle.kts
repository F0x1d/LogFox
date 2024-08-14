plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.filters"

dependencies {
    implementation(projects.feature.appsPicker)
    implementation(projects.feature.filtersCore)

    implementation(libs.gson)
}
