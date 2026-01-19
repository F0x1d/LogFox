package com.f0x1d.logfox.buildlogic.main.feature

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("logfox.android.library")
            apply("logfox.android.hilt")
        }
    }
}
