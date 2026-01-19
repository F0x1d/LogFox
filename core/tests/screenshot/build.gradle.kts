plugins {
    id("logfox.android.library")
    id("logfox.android.compose")
}

android.namespace = "com.f0x1d.logfox.core.tests.screenshot"

dependencies {
    api(projects.core.tests.compose)

    implementation(libs.bundles.androidx)
    implementation(libs.timber)

    api(libs.androidx.test.core)
    api(libs.robolectric)
    implementation(libs.roborazzi)
    implementation(libs.roborazzi.rule)
    implementation(libs.roborazzi.compose)

    implementation(libs.androidx.compose.ui.test.manifest)
}
