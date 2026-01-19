plugins {
    id("logfox.android.library")
    id("logfox.android.compose")
}

android.namespace = "com.f0x1d.logfox.compose.base"

dependencies {
    implementation(libs.bundles.androidx)
}
