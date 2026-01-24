plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.apps.picker.impl"

dependencies {
    api(projects.feature.appsPicker.api)

    implementation(libs.bundles.androidx)
}
