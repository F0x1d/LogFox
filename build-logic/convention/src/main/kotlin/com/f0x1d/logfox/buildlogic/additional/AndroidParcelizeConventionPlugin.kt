package com.f0x1d.logfox.buildlogic.additional

import com.f0x1d.logfox.buildlogic.extensions.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidParcelizeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(pluginId("kotlin-parcelize"))
        }
    }
}
