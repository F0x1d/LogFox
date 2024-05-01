plugins {
    id("logfox.android.library")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.arch"

dependencies {
    implementation(libs.bundles.androidx)
    implementation(libs.gson)
}
