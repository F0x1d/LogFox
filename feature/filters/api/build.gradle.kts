plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.filters.api"

dependencies {
    api(projects.core.recycler)
    api(projects.core.utils)
    api(projects.feature.logging.api)

    implementation(libs.bundles.androidx)
}
