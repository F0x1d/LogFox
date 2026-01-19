plugins {
    alias(libs.plugins.logfox.android.feature)
    alias(libs.plugins.logfox.android.room)
}

android {
    namespace = "com.f0x1d.logfox.feature.database.impl"
    defaultConfig {
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
}

dependencies {
    api(projects.feature.database.api)
    compileOnly(libs.androidx.compose.runtime)
    implementation(libs.bundles.androidx)
    implementation(libs.timber)
}
