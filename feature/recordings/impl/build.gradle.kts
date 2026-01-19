plugins {
    alias(libs.plugins.logfox.android.feature)
}

android.namespace = "com.f0x1d.logfox.feature.recordings.impl"

dependencies {
    api(projects.feature.recordings.api)
    implementation(projects.feature.recordings.presentation)
    implementation(projects.feature.logging.api)
    implementation(projects.feature.notifications.api)

    implementation(projects.core.di)
    implementation(projects.core.context)
    implementation(projects.feature.database.api)
    implementation(projects.feature.datetime.api)
    implementation(projects.feature.preferences.api)
    implementation(projects.feature.terminals.api)
    implementation(projects.core.ui.icons)
    implementation(projects.strings)
}
