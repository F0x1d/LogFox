plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.crashes"

dependencies {
    implementation(projects.feature.appsPicker)
    implementation(projects.feature.crashesCore)

    implementation(libs.glide)
}
