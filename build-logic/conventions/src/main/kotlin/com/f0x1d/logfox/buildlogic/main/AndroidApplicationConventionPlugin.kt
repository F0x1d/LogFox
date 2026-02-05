package com.f0x1d.logfox.buildlogic.main

import com.android.build.api.dsl.ApplicationExtension
import com.f0x1d.logfox.buildlogic.extensions.configureKotlinAndroid
import com.f0x1d.logfox.buildlogic.extensions.pluginId
import com.f0x1d.logfox.buildlogic.extensions.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("android-application"))
        }

        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)

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

            buildFeatures {
                buildConfig = true
            }

            dependenciesInfo {
                // https://gitlab.com/IzzyOnDroid/repo/-/issues/569#note_1997934495
                includeInApk = false
                includeInBundle = false
            }
        }
    }
}
