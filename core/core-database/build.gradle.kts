plugins {
    id("logfox.android.library")
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
    implementation(project(":data"))
    implementation(project(":core:core-arch"))

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}
