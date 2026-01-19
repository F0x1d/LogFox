plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.logging.service"

dependencies {
    implementation(projects.feature.crashes.api)
    implementation(projects.feature.filters.api)
    implementation(projects.feature.logging.api)
    implementation(projects.feature.notifications.api)
    implementation(projects.feature.recordings.api)

    implementation(projects.core.di)
    implementation(projects.core.context)
    implementation(projects.feature.database.api)
    implementation(projects.feature.preferences.api)
    implementation(projects.feature.terminals.api)
    implementation(projects.core.presentation)
    implementation(projects.strings)

    implementation(libs.bundles.androidx)
    implementation(libs.timber)
}
