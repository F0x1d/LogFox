package main.feature

import extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("logfox.android.feature")
            apply("logfox.android.compose")
        }

        dependencies {
            implementation(project(":core:core-ui-compose"))
        }
    }
}
