package com.f0x1d.logfox.buildlogic.additional.tests

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.f0x1d.logfox.buildlogic.extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidSnapshotTestsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("roborazzi"))
        }

        listOf(
            LibraryExtension::class.java,
            ApplicationExtension::class.java,
        ).forEach { extensionClass ->
            extensions.findByType(extensionClass)?.configureRobolectric()
        }
    }

    private fun CommonExtension<*, *, *, *, *, *>.configureRobolectric() {
        testOptions {
            animationsDisabled = true
            unitTests.isIncludeAndroidResources = true
        }
    }
}
