plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.compose)
}

android.namespace = "com.f0x1d.logfox.core.tests.compose"

dependencies {
    api(libs.androidx.compose.ui.test.junit4)
}
