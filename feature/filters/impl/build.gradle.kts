plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.filters.impl"

dependencies {
    implementation(projects.feature.appsPicker.api)
    implementation(projects.feature.filters.api)

    implementation(projects.core.di)
    implementation(projects.feature.database.api)
    implementation(projects.feature.logging.api)

    implementation(libs.gson)

}
