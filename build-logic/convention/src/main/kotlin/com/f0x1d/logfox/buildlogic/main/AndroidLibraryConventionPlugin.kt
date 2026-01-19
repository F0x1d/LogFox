package com.f0x1d.logfox.buildlogic.main

import com.android.build.gradle.LibraryExtension
import com.f0x1d.logfox.buildlogic.extensions.configureKotlinAndroid
import com.f0x1d.logfox.buildlogic.extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("android-library"))
            apply(pluginId("kotlin-android"))
        }

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
        }
    }
}
