plugins {
    id("logfox.android.core")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.ui"

dependencies {
    implementation(projects.core.preferences)

    implementation(libs.insetter)
    implementation(libs.viewpump)

    implementation(libs.glide)
    ksp(libs.glide.compiler)
}
