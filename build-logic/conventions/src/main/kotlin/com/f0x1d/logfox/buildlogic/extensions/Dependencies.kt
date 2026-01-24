package com.f0x1d.logfox.buildlogic.extensions

import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

internal fun DependencyHandlerScope.implementation(dependency: Any): Dependency? = add(
    configurationName = "implementation",
    dependencyNotation = dependency,
)

internal fun DependencyHandlerScope.ksp(dependency: Any): Dependency? = add(
    configurationName = "ksp",
    dependencyNotation = dependency,
)
