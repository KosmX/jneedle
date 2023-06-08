plugins {
    java
    val kotlinVersion = "1.8.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

group = "dev.kosmx.jarchecker"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

sourceSets {
    val main by getting
    val dbGen by creating {
        compileClasspath += main.compileClasspath
        runtimeClasspath += main.runtimeClasspath
        compileClasspath += main.output
        runtimeClasspath += main.output
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")

    implementation("org.ow2.asm:asm:${project.property("asm_version")}")
    implementation("org.ow2.asm:asm-tree:${project.property("asm_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    implementation("com.github.Col-E:CAFED00D:1.10.2")
    implementation("org.slf4j:slf4j-jdk14:2.0.7")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(java.targetCompatibility.majorVersion.toInt())
}

tasks {

    test {
        useJUnitPlatform()
    }

    withType<JavaCompile>().configureEach {
        options.release.set(java.targetCompatibility.majorVersion.toInt())
    }

    java {
        withSourcesJar()
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName.get()}" }
        }
        manifest {
            attributes("Main-Class" to "dev.kosmx.discordBot.MainKt")
        }
        archiveClassifier.set("slim")
    }

    shadowJar {
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }

    create<JavaExec>("buildDb") {
        this.mainClass.set("dev.kosmx.needle.dbGen.GeneratorKt")
        this.classpath = sourceSets["dbGen"].runtimeClasspath
    }
}