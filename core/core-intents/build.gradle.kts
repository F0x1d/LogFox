plugins {
    id("logfox.android.core")
}

android.namespace = "com.f0x1d.logfox.intents"

dependencies {
    implementation(project(":core:core-arch"))
}
