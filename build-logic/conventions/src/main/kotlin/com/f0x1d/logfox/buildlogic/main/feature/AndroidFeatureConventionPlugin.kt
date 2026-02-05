package com.f0x1d.logfox.buildlogic.main.feature

import com.f0x1d.logfox.buildlogic.additional.AndroidHiltConventionPlugin
import com.f0x1d.logfox.buildlogic.main.AndroidLibraryConventionPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(AndroidLibraryConventionPlugin::class.java)
            apply(AndroidHiltConventionPlugin::class.java)
        }
    }
}
