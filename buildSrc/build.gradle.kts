plugins {
    `kotlin-dsl`
    // alias(libs.plugins.kotlin.jvm)
}

repositories {
    maven("https://maven.solo-studios.ca/releases/")
    mavenCentral()
    // for kotlin-dsl plugin
    gradlePluginPortal()
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(gradlePlugin("org.jetbrains.dokka", libs.versions.kotlin))
    implementation(gradlePlugin("org.jetbrains.kotlin.jvm", libs.versions.kotlin))
    implementation(gradlePlugin("org.ajoberstar.grgit", libs.versions.grgit))
}

fun gradlePlugin(id: String, version: Provider<String>): String {
    return "$id:$id.gradle.plugin:${version.get()}"
}
