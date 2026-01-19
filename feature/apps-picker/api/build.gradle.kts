plugins {
    alias(libs.plugins.logfox.android.library)
}

android.namespace = "com.f0x1d.logfox.feature.apps.picker.api"

dependencies {
    implementation(projects.core.recycler)
    implementation(projects.strings)

    implementation(libs.bundles.androidx)
}
