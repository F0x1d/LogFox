plugins {
    id("logfox.android.library")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.preferences"

dependencies {
    implementation(project(":data"))

    implementation(project(":core:core-database"))

    implementation(libs.bundles.androidx)
}
