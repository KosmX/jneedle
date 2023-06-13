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
//include(":cli") // TODO: 2023-06-12 Move cli shit into cli
include(":gui")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

