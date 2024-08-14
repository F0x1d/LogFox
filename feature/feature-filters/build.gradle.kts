plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.filters"

dependencies {
    implementation(project(":feature:feature-filters-core"))
    implementation(project(":feature:feature-apps-picker"))

    implementation(libs.gson)

    implementation(libs.glide)
    ksp(libs.glide.compiler)
}
