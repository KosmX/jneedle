plugins {
    jneedle.compilation
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.compose)

    jneedle.shadow
    jneedle.repositories
    jneedle.tasks
    jneedle.publishing
}

version = rootProject.version

val mainClassName = "dev.kosmx.needle.compose.ComposeMainKt"
ext["mainClass"] = mainClassName

dependencies {
    implementation(projects.api)
    implementation(libs.logback)
    implementation(libs.slf4k)

    implementation(compose.desktop.currentOs)

    implementation(libs.mpfilepicker.desktop)

    implementation(libs.kotlinx.datetime)
}

compose.desktop {
    application {
        mainClass = mainClassName
    }
}
