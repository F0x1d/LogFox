package com.f0x1d.logfox.buildlogic.additional

import com.android.build.api.dsl.CommonExtension
import com.f0x1d.logfox.buildlogic.extensions.bundle
import com.f0x1d.logfox.buildlogic.extensions.implementation
import com.f0x1d.logfox.buildlogic.extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("compose-compiler"))
        }

        extensions.configure<CommonExtension> {
            buildFeatures.compose = true
        }

        dependencies {
            implementation(bundle("androidx-compose"))
        }
    }
}
