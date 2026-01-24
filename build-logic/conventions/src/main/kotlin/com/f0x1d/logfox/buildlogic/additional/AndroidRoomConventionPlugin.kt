package com.f0x1d.logfox.buildlogic.additional

import com.f0x1d.logfox.buildlogic.extensions.implementation
import com.f0x1d.logfox.buildlogic.extensions.ksp
import com.f0x1d.logfox.buildlogic.extensions.library
import com.f0x1d.logfox.buildlogic.extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("ksp"))
        }

        dependencies {
            implementation(library("androidx-room"))
            implementation(library("androidx-room-runtime"))
            ksp(library("androidx-room-compiler"))
        }
    }
}
