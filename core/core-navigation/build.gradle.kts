plugins {
    id("logfox.android.core")
}

android.namespace = "com.f0x1d.logfox.navigation"

dependencies {
    implementation(libs.bundles.androidx.navigation)
}
