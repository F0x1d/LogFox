package com.f0x1d.logfox.buildlogic.main

import com.f0x1d.logfox.buildlogic.extensions.JVM_VERSION
import com.f0x1d.logfox.buildlogic.extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class KotlinJvmConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("kotlin-jvm"))
        }

        extensions.configure(KotlinJvmProjectExtension::class.java) {
            jvmToolchain(JVM_VERSION)

            compilerOptions {
                freeCompilerArgs.add("-Xannotation-default-target=param-property")
            }
        }
    }
}
