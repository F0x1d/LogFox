plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.preferences.impl"

dependencies {
    api(projects.feature.preferences.api)

    implementation(projects.core.preferences.impl)
    implementation(projects.feature.terminals.api)

    implementation(libs.bundles.androidx)
    implementation(libs.flow.preferences)
}
