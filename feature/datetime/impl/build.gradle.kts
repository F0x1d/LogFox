plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.datetime.impl"

dependencies {
    implementation(projects.feature.datetime.api)

    implementation(projects.feature.preferences.api)
    implementation(projects.strings)

    implementation(libs.bundles.androidx)
    implementation(libs.timber)
}
