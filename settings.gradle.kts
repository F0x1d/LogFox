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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":app",
    ":data",
    ":strings",
)

private val modulesDirectories = setOf("core", "feature")
private val submoduleNameRegex = "^[A-Za-z0-9\\-_]+\$".toRegex()

requireNotNull(rootDir.listFiles()).filter { file ->
    file.isDirectory && file.name in modulesDirectories
}.forEach { file ->
    val modules = requireNotNull(file.listFiles())

    modules.filter(File::isDirectory).forEach { moduleFile ->
        if (submoduleNameRegex.matches(moduleFile.name)) {
            include(":${file.name}:${moduleFile.name}")
        }
    }
}
