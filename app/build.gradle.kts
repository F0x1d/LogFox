plugins {
    id("logfox.android.application")
}

android {
    val logFoxPackageName = "com.f0x1d.logfox"

    namespace = logFoxPackageName
    defaultConfig {
        applicationId = logFoxPackageName

        versionCode = 62
        versionName = "2.0.2"
    }
}

dependencies {
    implementation(projects.feature.crashes)
    implementation(projects.feature.filters)
    implementation(projects.feature.logging)
    implementation(projects.feature.recordings)
    implementation(projects.feature.settings)
    implementation(projects.feature.setup)

    implementation(libs.viewpump)
    implementation(libs.coil)

    implementation(libs.bundles.androidx)
    implementation(libs.material)

    implementation(libs.bundles.androidx.navigation)
}
