package com.f0x1d.logfox.buildlogic.additional

import com.f0x1d.logfox.buildlogic.extensions.implementation
import com.f0x1d.logfox.buildlogic.extensions.ksp
import com.f0x1d.logfox.buildlogic.extensions.library
import com.f0x1d.logfox.buildlogic.extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("ksp"))
            apply(pluginId("hilt-android"))
        }

        dependencies {
            implementation(library("hilt-android"))
            ksp(library("hilt-compiler"))
        }
    }
}
