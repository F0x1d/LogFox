plugins {
    id("logfox.android.application")
}

android {
    val logFoxPackageName = "com.f0x1d.logfox"

    namespace = logFoxPackageName
    defaultConfig {
        applicationId = logFoxPackageName

        versionCode = 60
        versionName = "2.0.0"
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":strings"))

    implementation(project(":core:core-arch"))
    implementation(project(":core:core-context"))
    implementation(project(":core:core-database"))
    implementation(project(":core:core-datetime"))
    implementation(project(":core:core-intents"))
    implementation(project(":core:core-io"))
    implementation(project(":core:core-navigation"))
    implementation(project(":core:core-preferences"))
    implementation(project(":core:core-terminals"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-ui-compose"))

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

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
