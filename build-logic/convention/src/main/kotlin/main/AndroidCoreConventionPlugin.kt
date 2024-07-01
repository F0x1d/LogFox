package main

import extensions.bundle
import extensions.implementation
import extensions.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

class AndroidCoreConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("logfox.android.library")
        }

        dependencies {
            implementation(project(":data"))
            implementation(project(":strings"))

            implementation(library("material"))
            implementation(bundle("androidx"))
        }
    }
}
