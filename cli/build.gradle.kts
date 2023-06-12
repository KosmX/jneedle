plugins {
    java
    signing
    application
    `java-library`
    `maven-publish`

    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.shadow)

    jneedle.repositories
    jneedle.tasks
    jneedle.publishing
    jneedle.compilation
}

val mainClassName = "dev.kosmx.needle.CliRunKt"
ext["mainClass"] = mainClassName


sourceSets {
    val main by getting
    create("dbGen") {
        compileClasspath += main.compileClasspath + main.output
        runtimeClasspath += main.runtimeClasspath + main.output
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.bundles.kotlinx.serialization)

    implementation(libs.bundles.kotlinx.coroutines)
    testImplementation(libs.bundles.kotlinx.coroutines.debugging)

    implementation(libs.kotlinx.cli)

    implementation(libs.bundles.asm)

    implementation(libs.cafedude)

    implementation(libs.slf4j)
    implementation(libs.slf4k)
    implementation(libs.logback)

    testImplementation(kotlin("test"))
}


tasks {
    withType<Jar>().configureEach {
        manifest {
            val javaAgent = "dev.kosmx.needle.launchWrapper.JavaAgentLauncher"
            attributes(
                "Premain-Class" to javaAgent,
                "Agent-Class" to javaAgent,
                "Can-Redefine-Classes" to "false",
                "Can-Retransform-Classes" to "false",
            )
        }
    }

    shadowJar {
        relocate("kotlin", "dev.kosmx.needle.kotlin")
        relocate("kotlinx", "dev.kosmx.needle.kotlinx")
        relocate("org.slf4j", "dev.kosmx.needle.org.slf4j")
    }

    // TODO: 2023-06-12 buildDb should be a subproject, or possibly a build script thingy
    create<JavaExec>("buildDb") {
        this.mainClass.set("dev.kosmx.needle.dbGen.GeneratorKt")
        this.classpath = sourceSets["dbGen"].runtimeClasspath
    }
}
