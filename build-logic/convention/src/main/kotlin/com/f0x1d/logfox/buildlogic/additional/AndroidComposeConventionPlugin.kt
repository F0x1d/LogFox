package com.f0x1d.logfox.buildlogic.additional

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.f0x1d.logfox.buildlogic.extensions.bundle
import com.f0x1d.logfox.buildlogic.extensions.implementation
import com.f0x1d.logfox.buildlogic.extensions.library
import com.f0x1d.logfox.buildlogic.extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("compose-compiler"))
        }

        listOf(
            LibraryExtension::class.java,
            ApplicationExtension::class.java,
        ).forEach { extensionClass ->
            extensions.findByType(extensionClass)?.enableCompose()
        }

        dependencies {
            implementation(library("kotlinx-immutable-collections"))
            implementation(bundle("androidx-compose"))
        }
    }

    private fun CommonExtension<*, *, *, *, *, *>.enableCompose() {
        buildFeatures.compose = true
    }
}
