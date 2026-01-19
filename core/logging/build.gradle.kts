plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.hilt)
}

android {
    namespace = "com.f0x1d.logfox.core.logging"
}

dependencies {
    implementation(projects.core.di)
    
    implementation(libs.timber)
}
