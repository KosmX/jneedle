pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "jneedle"
include(":api")
include(":cli")
include(":launchwrapper")
include(":gui")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

