import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.f0x1d.logfox.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(projectLibs.android.gradle.plugin)
    compileOnly(projectLibs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "logfox.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "logfox.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCore") {
            id = "logfox.android.core"
            implementationClass = "AndroidCoreConventionPlugin"
        }
        register("androidHilt") {
            id = "logfox.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFeature") {
            id = "logfox.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}
