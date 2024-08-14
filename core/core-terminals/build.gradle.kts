plugins {
    id("logfox.android.core")
    id("logfox.android.hilt")
}

android {
    namespace = "com.f0x1d.logfox.terminals"

    buildFeatures.aidl = true
}

dependencies {
    implementation(projects.core.coreArch)

    implementation(libs.libsu)
    implementation(libs.bundles.shizuku)
}
