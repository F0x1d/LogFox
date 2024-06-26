plugins {
    id("logfox.android.feature")
}

android.namespace = "com.f0x1d.logfox.feature.recordings"

dependencies {
    implementation(project(":feature:feature-recordings-core"))
}
