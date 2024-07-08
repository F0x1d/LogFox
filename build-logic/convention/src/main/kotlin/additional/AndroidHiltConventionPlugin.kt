package additional

import extensions.implementation
import extensions.ksp
import extensions.library
import extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("hilt-android"))
        }

        dependencies {
            implementation(library("hilt-android"))
            ksp(library("hilt-compiler"))
            implementation(library("androidx-hilt-navigation-fragment"))
        }
    }
}
