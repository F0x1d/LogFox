package com.f0x1d.logfox.buildlogic.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) = with(commonExtension) {
    compileSdk = version("compileSdk")

    defaultConfig {
        minSdk = version("minSdk")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        disable.add("NullSafeMutableLiveData")
    }

    extensions.configure<KotlinAndroidProjectExtension>("kotlin") {
        jvmToolchain(JVM_VERSION)
    }
}
