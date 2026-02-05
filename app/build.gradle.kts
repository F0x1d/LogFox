import com.android.build.api.variant.impl.VariantOutputImpl

plugins {
    alias(libs.plugins.logfox.android.application)
    alias(libs.plugins.logfox.android.hilt)
}

android {
    val logFoxPackageName = "com.f0x1d.logfox"

    namespace = logFoxPackageName
    defaultConfig {
        applicationId = logFoxPackageName

        versionCode = providers
            .environmentVariable("VERSION_CODE")
            .orNull
            ?.toIntOrNull()
            ?: Int.MAX_VALUE
        versionName = providers
            .environmentVariable("VERSION_NAME")
            .getOrElse("unknown")
    }

    buildFeatures {
        viewBinding = true
    }
}

androidComponents.onVariants { variant ->
    variant.outputs.forEach { output ->
        if (output is VariantOutputImpl) {
            output.outputFileName.set("LogFox-${output.versionName.get()}-${variant.name}.apk")
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

    implementation(projects.feature.export.impl)

    implementation(projects.feature.crashes.impl)
    implementation(projects.feature.crashes.presentation)

    implementation(projects.feature.filters.impl)
    implementation(projects.feature.filters.presentation)

    implementation(projects.feature.logging.impl)
    implementation(projects.feature.logging.presentation)

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
