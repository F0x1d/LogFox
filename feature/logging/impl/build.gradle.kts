plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.logging.impl"

dependencies {
    implementation(projects.feature.logging.api)

    implementation(projects.feature.database.api)
    implementation(projects.core.di)
    implementation(projects.feature.preferences.api)
    implementation(projects.feature.terminals.api)

    implementation(libs.timber)
}
