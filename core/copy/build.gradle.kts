plugins {
    alias(libs.plugins.logfox.android.library)
}

android {
    namespace = "com.f0x1d.logfox.core.copy"
}

dependencies {
    implementation(projects.core.context)
    implementation(projects.strings)
}
