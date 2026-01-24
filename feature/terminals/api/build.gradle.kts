plugins {
    alias(libs.plugins.logfox.android.library)
    alias(libs.plugins.logfox.android.parcelize)
}

android.namespace = "com.f0x1d.logfox.feature.terminals.api"

dependencies {
    implementation(libs.javax.inject)

    implementation(libs.bundles.androidx)
}
