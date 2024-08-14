plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.crashes"

dependencies {
    implementation(projects.feature.featureAppsPicker)
    implementation(projects.feature.featureCrashesCore)

    implementation(libs.glide)
}
