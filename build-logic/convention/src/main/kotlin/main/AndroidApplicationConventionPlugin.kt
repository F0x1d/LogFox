package main

import com.android.build.api.dsl.ApplicationExtension
import extensions.configureKotlinAndroid
import extensions.configureRobolectric
import extensions.coreDependencies
import extensions.pluginId
import extensions.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager){
            apply(pluginId("android-application"))
            apply(pluginId("kotlin-android"))
            apply(pluginId("kotlin-parcelize"))
            apply(pluginId("ksp"))

            apply("logfox.android.hilt")
            apply("logfox.android.compose")
            apply("logfox.android.unitTests")
        }

        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            configureRobolectric()

            defaultConfig {
                targetSdk = version("targetSdk")

                val releaseSigningConfigName = "release"
                val keyStoreFile = file("keystore/main.jks")

                if (keyStoreFile.exists()) {
                    signingConfigs {
                        create(releaseSigningConfigName) {
                            storeFile = keyStoreFile
                            storePassword = System.getenv("KEY_STORE_PASSWORD")
                            keyAlias = System.getenv("ALIAS")
                            keyPassword = System.getenv("KEY_PASSWORD")
                        }
                    }
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = true

                        if (keyStoreFile.exists()) {
                            signingConfig = signingConfigs.getByName(releaseSigningConfigName)
                        }

                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                        )
                    }

                    debug {
                        applicationIdSuffix = ".debug"
                    }
                }
            }

            dependenciesInfo {
                // https://gitlab.com/IzzyOnDroid/repo/-/issues/569#note_1997934495
                includeInApk = false
                includeInBundle = false
            }
        }

        dependencies { coreDependencies() }
    }
}
