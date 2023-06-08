plugins {
    val kotlinVersion = "1.8.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jetbrains.compose") version "1.4.0"
}

dependencies {
    implementation(rootProject)
    implementation(compose.desktop.currentOs)
    implementation("com.darkrockstudios:mpfilepicker-desktop:1.1.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}

kotlin {
    jvmToolchain(17)
}
