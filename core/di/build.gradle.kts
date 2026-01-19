plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.hilt)
}

android {
    namespace = "com.f0x1d.logfox.core.di"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
