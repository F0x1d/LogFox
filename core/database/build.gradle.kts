plugins {
    id("logfox.android.core")
    id("logfox.android.hilt")
}

android {
    namespace = "com.f0x1d.logfox.database"

    defaultConfig {
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
}

dependencies {
    implementation(projects.core.arch)

    compileOnly(libs.androidx.compose.runtime)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}
