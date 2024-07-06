plugins {
    id("logfox.android.core")
    id("logfox.android.compose")
}

android.namespace = "com.f0x1d.logfox.ui.compose"

dependencies {
    implementation(project(":core:core-ui"))
}
