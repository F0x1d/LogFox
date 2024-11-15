plugins {
    id("logfox.android.core")

    id("logfox.android.hilt")
    id("logfox.android.compose")
}

android.namespace = "com.f0x1d.logfox.arch"

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.uiCompose)
    implementation(projects.core.preferences)

    implementation(libs.insetter)
    implementation(libs.viewpump)
}
