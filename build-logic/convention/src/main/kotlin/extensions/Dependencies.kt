package extensions

import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

internal fun DependencyHandlerScope.implementation(dependency: Any): Dependency? = add(
    configurationName = "implementation",
    dependencyNotation = dependency,
)

internal fun DependencyHandlerScope.debugImplementation(dependency: Any): Dependency? = add(
    configurationName = "debugImplementation",
    dependencyNotation = dependency,
)

internal fun DependencyHandlerScope.testImplementation(dependency: Any): Dependency? = add(
    configurationName = "testImplementation",
    dependencyNotation = dependency,
)

internal fun DependencyHandlerScope.implementation(bundle: List<Any>): List<Dependency?> = bundle.map {
    add(
        configurationName = "implementation",
        dependencyNotation = it,
    )
}

internal fun DependencyHandlerScope.ksp(dependency: Any): Dependency? = add(
    configurationName = "ksp",
    dependencyNotation = dependency,
)
