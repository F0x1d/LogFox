plugins {
    id("logfox.android.feature")
}

android {
    namespace = "com.f0x1d.logfox.feature.logging"

    buildFeatures.buildConfig = true
}

dependencies {
    implementation(project(":feature:feature-logging-core"))
    implementation(project(":feature:feature-crashes-core"))
}
