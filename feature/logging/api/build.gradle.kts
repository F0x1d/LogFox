plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.logging.api"

dependencies {
    implementation(projects.feature.terminals.api)
    api(projects.core.recycler)

    implementation(libs.bundles.androidx)
    implementation(libs.timber)
}
