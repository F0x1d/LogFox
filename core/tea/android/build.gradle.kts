plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.compose)
}

android {
    namespace = "com.f0x1d.logfox.core.tea.android"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(projects.core.tea.base)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.lifecycle.runtime)
    api(libs.androidx.fragment)
    api(libs.androidx.preference)
    api(libs.material)
    
    implementation(libs.androidx.compose.runtime)
}
