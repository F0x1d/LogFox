package com.f0x1d.logfox.buildlogic.additional.tests

import com.android.build.api.dsl.CommonExtension
import com.f0x1d.logfox.buildlogic.extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidSnapshotTestsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("roborazzi"))
        }

        extensions.configure<CommonExtension> {
            with(testOptions) {
                animationsDisabled = true
                unitTests.isIncludeAndroidResources = true
            }
        }
    }
}
