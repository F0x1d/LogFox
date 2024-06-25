import com.android.build.api.dsl.ApplicationExtension
import extensions.configureKotlinAndroid
import extensions.pluginId
import extensions.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager){
            apply(pluginId("android-application"))
            apply(pluginId("kotlin-android"))
            apply(pluginId("kotlin-parcelize"))
            apply(pluginId("ksp"))

            apply("logfox.android.hilt")
            apply("logfox.android.navigation.safeargs")
        }

        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)

            defaultConfig {
                targetSdk = version("targetSdk")

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = true

                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                        )
                    }

                    debug {
                        applicationIdSuffix = ".debug"
                    }
                }
            }
        }
    }
}
