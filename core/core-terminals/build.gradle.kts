plugins {
    id("logfox.android.core")
    id("logfox.android.hilt")
}

android {
    namespace = "com.f0x1d.logfox.terminals"

    buildFeatures.aidl = true
}

dependencies {
    implementation(project(":core:core-arch"))

    implementation(libs.bundles.shizuku)
}
