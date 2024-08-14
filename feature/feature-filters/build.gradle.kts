plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.filters"

dependencies {
    implementation(projects.feature.featureAppsPicker)
    implementation(projects.feature.featureFiltersCore)

    implementation(libs.gson)
}
