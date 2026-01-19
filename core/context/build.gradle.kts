plugins {
    alias(libs.plugins.logfox.android.library)
}

android {
    namespace = "com.f0x1d.logfox.core.context"
}

dependencies {
    api(projects.core.compat)

    implementation(projects.strings)
    api(libs.androidx.core)
}
