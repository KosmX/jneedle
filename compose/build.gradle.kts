import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    val kotlinVersion = "1.8.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jetbrains.compose") version "1.4.0"
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8


dependencies {
    implementation(rootProject)
    implementation(compose.desktop.currentOs)
    implementation("com.darkrockstudios:mpfilepicker-desktop:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}

compose.desktop {
    application {
        mainClass = "dev.kosmx.needle.compose.ComposeMainKt"
    }
}

kotlin {
    jvmToolchain(8)
}

version = rootProject.version

tasks {

    withType<JavaCompile>().configureEach {
        options.release.set(java.targetCompatibility.majorVersion.toInt())
    }

    archivesName.set("jneedle-gui")
    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName.get()}" }
        }
        manifest {
            attributes(
                "Main-Class" to "dev.kosmx.needle.compose.ComposeMainKt",
            )
        }
        archiveClassifier.set("slim")
    }

    shadowJar {
        //configurations = listOf(project.configurations["shadowImplement"])
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }
}
