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
    implementation(projects.feature.featureCrashes)
    implementation(projects.feature.featureCrashesCore)
    implementation(projects.feature.featureFilters)
    implementation(projects.feature.featureFiltersCore)
    implementation(projects.feature.featureLogging)
    implementation(projects.feature.featureLoggingCore)
    implementation(projects.feature.featureRecordings)
    implementation(projects.feature.featureRecordingsCore)
    implementation(projects.feature.featureSettings)
    implementation(projects.feature.featureSetup)

    implementation(libs.insetter)
    implementation(libs.bundles.shizuku)
    implementation(libs.viewpump)
    implementation(libs.gson)

    implementation(libs.coil)
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.bundles.androidx)
    implementation(libs.material)

    implementation(libs.bundles.androidx.navigation)
}
