plugins {
    alias(libs.plugins.logfox.android.library)
}

android.namespace = "com.f0x1d.logfox.feature.apps.picker.api"

dependencies {
    implementation(projects.strings)

    implementation(libs.bundles.androidx)
    api(libs.kotlinx.immutable.collections)
}
