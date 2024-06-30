plugins {
    id("logfox.android.core")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.arch"

dependencies {
    implementation(libs.insetter)
    implementation(libs.viewpump)
    implementation(libs.gson)
}
