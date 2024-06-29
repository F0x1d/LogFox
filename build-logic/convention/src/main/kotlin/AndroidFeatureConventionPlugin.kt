import extensions.bundle
import extensions.implementation
import extensions.ksp
import extensions.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply {
            apply("logfox.android.library")
            apply("logfox.android.hilt")
        }

        dependencies {
            implementation(project(":data"))
            implementation(project(":strings"))

            implementation(project(":core:core-arch"))
            implementation(project(":core:core-context"))
            implementation(project(":core:core-database"))
            implementation(project(":core:core-datetime"))
            implementation(project(":core:core-intents"))
            implementation(project(":core:core-io"))
            implementation(project(":core:core-navigation"))
            implementation(project(":core:core-preferences"))
            implementation(project(":core:core-terminals"))
            implementation(project(":core:core-ui"))

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
