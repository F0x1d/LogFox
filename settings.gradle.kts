pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
    }
}

rootProject.name = "LogFox"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":strings")

includeRecursive(File("core"))
includeRecursive(File("feature"))

private fun includeRecursive(
    directory: File,
    parentDirectoriesNames: List<String> = listOf(directory.name),
) {
    fun File.isModule(): Boolean = File(this, "build.gradle.kts").isFile

    if (directory.isModule()) {
        val moduleName = parentDirectoriesNames.joinToString(
            prefix = ":",
            separator = ":",
        )

        include(moduleName)
    } else {
        directory
            .listFiles()
            ?.forEach { file ->
                includeRecursive(
                    directory = file,
                    parentDirectoriesNames = parentDirectoriesNames + file.name,
                )
            }
    }
}
