plugins {
    id("logfox.android.application")
}

android {
    val logFoxPackageName = "com.f0x1d.logfox"

    namespace = logFoxPackageName
    defaultConfig {
        applicationId = logFoxPackageName

        versionCode = 61
        versionName = "2.0.1"
    }
}

dependencies {
    implementation(project(":feature:feature-crashes"))
    implementation(project(":feature:feature-crashes-core"))
    implementation(project(":feature:feature-filters"))
    implementation(project(":feature:feature-filters-core"))
    implementation(project(":feature:feature-logging"))
    implementation(project(":feature:feature-logging-core"))
    implementation(project(":feature:feature-recordings"))
    implementation(project(":feature:feature-recordings-core"))
    implementation(project(":feature:feature-settings"))
    implementation(project(":feature:feature-setup"))

    implementation(libs.insetter)
    implementation(libs.bundles.shizuku)
    implementation(libs.viewpump)
    implementation(libs.gson)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.bundles.androidx)
    implementation(libs.material)

    implementation(libs.bundles.androidx.navigation)
}
