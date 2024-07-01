plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.crashes"

dependencies {
    implementation(project(":feature:feature-crashes-core"))

    implementation(libs.glide)
    ksp(libs.glide.compiler)
}
