plugins {
    id("logfox.android.library")
    id("logfox.android.hilt")
}

android.namespace = "com.f0x1d.logfox.context"

dependencies {
    implementation(project(":strings"))
    implementation(project(":core:core-arch"))

    implementation(libs.bundles.androidx)
}
