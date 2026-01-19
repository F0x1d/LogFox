plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.setup.api"

dependencies {
    implementation(projects.feature.terminals.api)
    implementation(libs.bundles.androidx)
}
