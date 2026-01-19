plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.f0x1d.logfox.core.ui.glide"
}

dependencies {
    api(projects.core.ui.icons)

    implementation(libs.glide)
    ksp(libs.glide.compiler)
}
