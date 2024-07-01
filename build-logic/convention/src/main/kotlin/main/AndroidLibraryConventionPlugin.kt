package main

import com.android.build.gradle.LibraryExtension
import extensions.configureKotlinAndroid
import extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("android-library"))
            apply(pluginId("kotlin-android"))
            apply(pluginId("kotlin-parcelize"))
            apply(pluginId("ksp"))
        }

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
        }
    }
}
