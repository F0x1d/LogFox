package com.f0x1d.logfox.buildlogic.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) = with(commonExtension) {
    compileSdk = version("compileSdk")

    with(defaultConfig) {
        minSdk = version("minSdk")
    }

    this@configureKotlinAndroid.extensions.configure<KotlinAndroidExtension> {
        jvmToolchain(JVM_VERSION)

        compilerOptions {
            freeCompilerArgs.add("-Xannotation-default-target=param-property")
        }
    }
}
