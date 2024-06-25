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

include(":app")

include(":data")
include(":strings")

include(":core:core-arch")
include(":core:core-context")
include(":core:core-database")
include(":core:core-datetime")
include(":core:core-intents")
include(":core:core-io")
include(":core:core-preferences")
include(":core:core-terminals")
include(":core:core-ui")

include(":feature:feature-crashes")
include(":feature:feature-crashes-core")
include(":feature:feature-logging")
include(":feature:feature-logging-core")
