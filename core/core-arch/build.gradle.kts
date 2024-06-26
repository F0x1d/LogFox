plugins {
    id("logfox.android.library")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.arch"

dependencies {
    implementation(project(":data"))
    implementation(project(":strings"))

    implementation(libs.material)
    implementation(libs.bundles.androidx)

    implementation(libs.insetter)
    implementation(libs.viewpump)
    implementation(libs.gson)
}
