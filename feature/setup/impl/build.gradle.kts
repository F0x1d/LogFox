plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.setup.impl"

dependencies {
    implementation(projects.feature.setup.api)

    implementation(projects.core.context)
    implementation(projects.feature.copy.impl)
    implementation(projects.feature.preferences.api)
    implementation(projects.feature.terminals.api)

    implementation(libs.bundles.androidx)
}
