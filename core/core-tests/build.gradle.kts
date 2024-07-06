plugins {
    id("logfox.android.core")
    id("logfox.android.compose")
    id("logfox.android.unitTests")
}

android.namespace = "com.f0x1d.logfox.core.tests"

dependencies {
    implementation(libs.androidx.test.core)
    implementation(libs.robolectric)
    implementation(libs.roborazzi)
    implementation(libs.roborazzi.rule)
    implementation(libs.roborazzi.compose)

    implementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.compose.ui.test.junit4)
}
