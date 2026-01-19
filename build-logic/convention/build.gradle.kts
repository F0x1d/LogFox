plugins {
    `kotlin-dsl`
}

group = "com.f0x1d.logfox.buildlogic"

dependencies {
    compileOnly(projectLibs.android.gradle.plugin)
    compileOnly(projectLibs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "logfox.android.application"
            implementationClass = "com.f0x1d.logfox.buildlogic.main.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "logfox.android.library"
            implementationClass = "com.f0x1d.logfox.buildlogic.main.AndroidLibraryConventionPlugin"
        }
        register("kotlinJvm") {
            id = "logfox.kotlin.jvm"
            implementationClass = "com.f0x1d.logfox.buildlogic.main.KotlinJvmConventionPlugin"
        }
        register("androidFeature") {
            id = "logfox.android.feature"
            implementationClass = "com.f0x1d.logfox.buildlogic.main.feature.AndroidFeatureConventionPlugin"
        }
        register("androidFeatureCompose") {
            id = "logfox.android.feature.compose"
            implementationClass = "com.f0x1d.logfox.buildlogic.main.feature.AndroidComposeFeatureConventionPlugin"
        }

        register("androidHilt") {
            id = "logfox.android.hilt"
            implementationClass = "com.f0x1d.logfox.buildlogic.additional.AndroidHiltConventionPlugin"
        }
        register("androidCompose") {
            id = "logfox.android.compose"
            implementationClass = "com.f0x1d.logfox.buildlogic.additional.AndroidComposeConventionPlugin"
        }
        register("androidParcelize") {
            id = "logfox.android.parcelize"
            implementationClass = "com.f0x1d.logfox.buildlogic.additional.AndroidParcelizeConventionPlugin"
        }
        register("androidRoom") {
            id = "logfox.android.room"
            implementationClass = "com.f0x1d.logfox.buildlogic.additional.AndroidRoomConventionPlugin"
        }
        register("androidTestsSnapshot") {
            id = "logfox.android.tests.snapshot"
            implementationClass = "com.f0x1d.logfox.buildlogic.additional.tests.AndroidSnapshotTestsConventionPlugin"
        }
    }
}
