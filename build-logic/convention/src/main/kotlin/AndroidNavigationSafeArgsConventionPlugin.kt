import extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidNavigationSafeArgsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(pluginId("androidx-navigation-safeargs"))
    }
}
