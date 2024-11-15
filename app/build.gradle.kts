plugins {
    id("logfox.android.application")
}

android {
    val logFoxPackageName = "com.f0x1d.logfox"

    namespace = logFoxPackageName
    defaultConfig {
        applicationId = logFoxPackageName

        versionCode = 65
        versionName = "2.0.5"
    }
}

dependencies {
    implementation(projects.feature.appsPicker.api)
    implementation(projects.feature.appsPicker.impl)

    implementation(projects.feature.crashes.appsList)
    implementation(projects.feature.crashes.details)
    implementation(projects.feature.crashes.impl)
    implementation(projects.feature.crashes.list)

    implementation(projects.feature.filters.edit)
    implementation(projects.feature.filters.impl)
    implementation(projects.feature.filters.list)

    implementation(projects.feature.logging.extendedCopy)
    implementation(projects.feature.logging.impl)
    implementation(projects.feature.logging.list)
    implementation(projects.feature.logging.search)
    implementation(projects.feature.logging.service)

    implementation(projects.feature.recordings.details)
    implementation(projects.feature.recordings.impl)
    implementation(projects.feature.recordings.list)

    implementation(projects.feature.settings)
    implementation(projects.feature.setup)

    implementation(libs.timber)
    implementation(libs.gson)
    implementation(libs.viewpump)
    implementation(libs.coil)

    implementation(libs.bundles.androidx)
    implementation(libs.material)

    implementation(libs.bundles.androidx.navigation)
}
