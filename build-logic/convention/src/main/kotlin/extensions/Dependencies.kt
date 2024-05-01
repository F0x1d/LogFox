package extensions

import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

internal fun DependencyHandlerScope.implementation(dependency: String): Dependency? = add(
    configurationName = "implementation",
    dependencyNotation = dependency,
)

internal fun DependencyHandlerScope.ksp(dependency: String): Dependency? = add(
    configurationName = "ksp",
    dependencyNotation = dependency,
)
