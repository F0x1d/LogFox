package com.f0x1d.logfox.buildlogic.main.feature

import com.f0x1d.logfox.buildlogic.additional.AndroidComposeConventionPlugin
import com.f0x1d.logfox.buildlogic.additional.tests.AndroidSnapshotTestsConventionPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidComposeFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(AndroidFeatureConventionPlugin::class.java)
            apply(AndroidComposeConventionPlugin::class.java)
            apply(AndroidSnapshotTestsConventionPlugin::class.java)
        }
    }
}
