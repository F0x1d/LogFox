package extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.version(alias: String): Int = libs.findVersion(alias).get().requiredVersion.toInt()
internal fun Project.pluginId(alias: String): String = libs.findPlugin(alias).get().get().pluginId
internal fun Project.library(alias: String): String = libs.findLibrary(alias).get().get().toString()
