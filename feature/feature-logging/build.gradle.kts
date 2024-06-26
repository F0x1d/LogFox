plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.logging"

dependencies {
    implementation(project(":feature:feature-crashes-core"))
    implementation(project(":feature:feature-logging-core"))
    implementation(project(":feature:feature-recordings-core"))
}
