package additional.tests

import extensions.debugImplementation
import extensions.library
import extensions.pluginId
import extensions.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidUnitTestsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("roborazzi"))
        }

        dependencies {
            debugImplementation(library("androidx-compose-ui-test-manifest"))
            testImplementation(library("androidx-compose-ui-test-junit4"))

            testImplementation(library("junit"))
            testImplementation(library("robolectric"))
            testImplementation(library("androidx-test-core"))

            testImplementation(library("roborazzi"))
            testImplementation(library("roborazzi-compose"))
            testImplementation(library("roborazzi-rule"))
        }
    }
}
