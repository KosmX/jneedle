plugins {
    java
    val kotlinVersion = "1.8.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
    signing // well, make sure it's me!
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

group = "dev.kosmx.needle"
version = project.property("version") as String

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

sourceSets {
    val main by getting
    create("dbGen") {
        compileClasspath += main.compileClasspath
        runtimeClasspath += main.runtimeClasspath
        compileClasspath += main.output
        runtimeClasspath += main.output
    }
}

configurations {
    val shadowImplement by creating
    val implementation by getting
    implementation.extendsFrom(shadowImplement)
}

dependencies {
    "shadowImplement"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    "shadowImplement"("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")

    "shadowImplement"("org.ow2.asm:asm:${project.property("asm_version")}")
    "shadowImplement"("org.ow2.asm:asm-tree:${project.property("asm_version")}")
    "shadowImplement"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    "shadowImplement"("com.github.Col-E:CAFED00D:1.10.2")
    "shadowImplement"("org.slf4j:slf4j-jdk14:2.0.7")

    "shadowImplement"(group = "software.coley", name = "lljzip", version = "1.6.1")
    //shadow("org.slf4j:slf4j:2.0.7")


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
        withJavadocJar()
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName.get()}" }
        }
        manifest {
            attributes(
                "Main-Class" to "dev.kosmx.needle.CliRunKt",
                "Premain-Class" to "dev.kosmx.needle.launchWrapper.JavaAgentLauncher",
                "Agent-Class" to "dev.kosmx.needle.launchWrapper.JavaAgentLauncher",
                "Can-Redefine-Classes" to "false",
                "Can-Retransform-Classes" to "false",
            )
        }
        //archiveClassifier.set("slim")
    }

    shadowJar {
        configurations = listOf(project.configurations["shadowImplement"])
        archiveClassifier.set("fat")
        relocate("kotlin", "dev.kosmx.needle.kotlin")
        relocate("kotlinx", "dev.kosmx.needle.kotlinx")
        relocate("org.slf4j", "dev.kosmx.needle.org.slf4j")
    }
    build {
        dependsOn(shadowJar)
    }

    create<JavaExec>("buildDb") {
        this.mainClass.set("dev.kosmx.needle.dbGen.GeneratorKt")
        this.classpath = sourceSets["dbGen"].runtimeClasspath
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = "jneedle"
            version = project.version as String
            from(components["java"])


            pom {
                name.set("jNeedle")
                description.set("java malware detection tool")
                url.set("https://github.com/KosmX/jneedle")
                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("https://mit-license.org/")
                    }
                }
                developers {
                    developer {
                        id.set("kosmx")
                        name.set("KosmX")
                        email.set("kosmx.mc@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/KosmX/jneedle.git")
                    url.set("https://github.com/KosmX/jneedle")
                }
            }
        }
    }
    repositories {
        maven {
            val release = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshot = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = (if ("SNAPSHOT" in version.toString()) snapshot else release)
            authentication {
                credentials {
                    username = "kosmx" // wow!
                    val passFile = File("maven.pass")
                    password = if (passFile.isFile) passFile.readText() else "nope"
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}
