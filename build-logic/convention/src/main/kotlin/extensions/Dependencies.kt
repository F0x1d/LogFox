package extensions

import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

internal fun DependencyHandlerScope.coreDependencies(withCompose: Boolean = true) {
    implementation(project(":data"))
    implementation(project(":strings"))

    implementation(project(":core:arch"))
    implementation(project(":core:database"))
    implementation(project(":core:datetime"))
    implementation(project(":core:navigation"))
    implementation(project(":core:preferences"))
    implementation(project(":core:terminals"))
    implementation(project(":core:ui"))

    if (withCompose) {
        implementation(project(":core:ui-compose"))
        testImplementation(project(":core:tests"))
    }
}

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

internal fun DependencyHandlerScope.androidTestImplementation(dependency: Any): Dependency? = add(
    configurationName = "androidTestImplementation",
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
