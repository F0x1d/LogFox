plugins {
    id("logfox.android.library")
    id("logfox.android.hilt")
}

android {
    namespace = "com.f0x1d.logfox.terminals"

    buildFeatures.aidl = true
}

dependencies {
    implementation(project(":data"))

    implementation(libs.bundles.shizuku)
}
