plugins {
    id("logfox.android.library")
    id("logfox.android.compose")
}

android.namespace = "com.f0x1d.logfox.compose.designsystem"

dependencies {
    api(projects.core.compose.base)

    implementation(projects.strings)

    implementation(libs.bundles.androidx)
    implementation(libs.timber)
}
