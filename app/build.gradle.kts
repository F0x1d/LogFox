plugins {
    alias(libs.plugins.logfox.android.application)
    alias(libs.plugins.logfox.android.hilt)
}

android {
    val logFoxPackageName = "com.f0x1d.logfox"

    namespace = logFoxPackageName
    defaultConfig {
        applicationId = logFoxPackageName

        versionCode = 69
        versionName = "2.1.1"
    }

    buildFeatures {
        viewBinding = true
    }

    applicationVariants.all {
        outputs.all {
            val gitSha = System.getenv("GIT_SHA") ?: "local"
            outputFileName = "LogFox-$versionName-${buildType.name}-$gitSha.apk"
        }
    }
}

dependencies {
    implementation(projects.feature.notifications.api)
    implementation(projects.strings)
    implementation(projects.core.tea.android)
    implementation(projects.core.ui.base)
    implementation(projects.core.ui.preference)
    implementation(projects.core.ui.icons)
    implementation(projects.core.ui.compose.fragment)
    implementation(projects.core.ui.glide)
    implementation(projects.core.ui.view)
    implementation(projects.core.context)
    implementation(projects.core.logging)
    implementation(projects.core.di)
    implementation(projects.core.utils)
    implementation(projects.feature.navigation.api)

    implementation(projects.feature.database.impl)

    implementation(projects.feature.terminals.impl)

    implementation(projects.feature.preferences.impl)
    implementation(projects.feature.preferences.presentation)

    implementation(projects.feature.appsPicker.impl)
    implementation(projects.feature.appsPicker.presentation)

    implementation(projects.feature.crashes.impl)
    implementation(projects.feature.crashes.presentation)

    implementation(projects.feature.filters.impl)
    implementation(projects.feature.filters.presentation)

    implementation(projects.feature.logging.impl)
    implementation(projects.feature.logging.presentation)
    implementation(projects.feature.logging.service)

    implementation(projects.feature.recordings.impl)
    implementation(projects.feature.recordings.presentation)

    implementation(projects.feature.setup.impl)
    implementation(projects.feature.setup.presentation)

    implementation(libs.timber)
    implementation(libs.gson)
    implementation(libs.viewpump)
    implementation(libs.coil)

    implementation(libs.bundles.androidx)
    implementation(libs.material)

    implementation(libs.bundles.androidx.navigation)
    implementation(libs.androidx.hilt.navigation.fragment)
}
