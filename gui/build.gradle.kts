plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.shadow)

    alias(libs.plugins.compose)

    jneedle.repositories
    jneedle.tasks
    jneedle.publishing
    jneedle.compilation
}

version = rootProject.version

val mainClassName = "dev.kosmx.needle.compose.ComposeMainKt"
ext["mainClass"] = mainClassName

dependencies {
    implementation(projects.api)

    implementation(compose.desktop.currentOs)

    implementation(libs.mpfilepicker.desktop)

    implementation(libs.kotlinx.datetime)
}

compose.desktop {
    application {
        mainClass = mainClassName
    }
}
