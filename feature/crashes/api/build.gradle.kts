plugins {
    id("logfox.android.library")
}

android.namespace = "com.f0x1d.logfox.feature.crashes.api"

dependencies {
    implementation(projects.feature.logging.api)
    implementation(projects.feature.database.api)
    implementation(projects.core.context)

    implementation(libs.bundles.androidx)
}
