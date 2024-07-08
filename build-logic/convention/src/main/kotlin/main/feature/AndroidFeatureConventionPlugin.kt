package main.feature

import extensions.bundle
import extensions.coreDependencies
import extensions.implementation
import extensions.ksp
import extensions.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("logfox.android.library")
            apply("logfox.android.hilt")
        }

        dependencies {
            coreDependencies(withCompose = false)

            implementation(library("material"))
            implementation(bundle("androidx"))
            implementation(bundle("androidx-navigation"))

            implementation(library("androidx-room"))
            implementation(library("androidx-room-runtime"))
            ksp(library("androidx-room-compiler"))

            implementation(library("insetter"))
            implementation(library("flow-preferences"))
        }
    }
}
