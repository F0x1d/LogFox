plugins {
    id("logfox.android.library")
}

android {
    namespace = "com.f0x1d.logfox.context"

    buildFeatures.buildConfig = true
}

dependencies {
    implementation(project(":strings"))
    implementation(project(":core:core-arch"))

    implementation(libs.bundles.androidx)
}
