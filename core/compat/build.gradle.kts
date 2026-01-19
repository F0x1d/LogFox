plugins {
    alias(libs.plugins.logfox.android.library)
}

android {
    namespace = "com.f0x1d.logfox.core.compat"
}

dependencies {
    implementation(libs.androidx.core)
}
