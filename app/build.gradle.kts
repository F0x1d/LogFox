import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    id("logfox.android.application")
}

android {
    val logFoxPackageName = "com.f0x1d.logfox"

    namespace = logFoxPackageName
    defaultConfig {
        applicationId = logFoxPackageName

        versionCode = 60
        versionName = "1.5.8"
    }

    applicationVariants.all {
       outputs.all {
            if (this is ApkVariantOutputImpl) {
                outputFileName = "logfox-$versionName.apk"
            }
        }
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":core:core-arch"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-database"))

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

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
