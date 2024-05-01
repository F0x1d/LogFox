plugins {
    id("logfox.android.library")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.ui"

dependencies {
    implementation(project(":data"))

    implementation(libs.insetter)
    implementation(libs.viewpump)
    implementation(libs.bundles.androidx)
    implementation(libs.material)

    implementation(libs.glide)
    ksp(libs.glide.compiler)
}
