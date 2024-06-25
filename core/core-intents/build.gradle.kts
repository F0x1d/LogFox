plugins {
    id("logfox.android.library")
}

android.namespace = "com.f0x1d.logfox.intents"

dependencies {
    implementation(project(":core:core-arch"))
}
