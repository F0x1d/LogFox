plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.crashes.impl"

dependencies {
    implementation(projects.feature.logging.api)
    implementation(projects.feature.notifications.api)
    implementation(projects.strings)
    implementation(projects.core.di)
    implementation(projects.core.context)
    implementation(projects.core.copy)
    implementation(projects.feature.database.api)
    implementation(projects.feature.navigation.api)
    implementation(projects.feature.preferences.api)
    implementation(projects.core.presentation)

    api(projects.feature.crashes.api)

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.navigation)
}
