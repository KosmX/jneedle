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

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    target {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_1_8.toString()
                languageVersion = "1.8"
                apiVersion = "1.8"
            }
        }
    }
}

dependencies {
    implementation(gradlePlugin("org.jetbrains.dokka", libs.versions.kotlin))
    implementation(gradlePlugin("org.jetbrains.kotlin.jvm", libs.versions.kotlin))
    implementation(gradlePlugin("org.ajoberstar.grgit", libs.versions.grgit))
}

fun gradlePlugin(id: String, version: Provider<String>): String {
    return "$id:$id.gradle.plugin:${version.get()}"
}
