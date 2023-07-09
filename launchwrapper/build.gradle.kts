plugins {
    application
    jneedle.compilation
    alias(libs.plugins.kotlin.serialization)

    jneedle.shadow
    jneedle.repositories
    jneedle.tasks
    jneedle.publishing
}


val mainClassName = "dev.kosmx.needle.launchWrapper.Launch"
ext["mainClass"] = mainClassName
application.mainClass.set(mainClassName)


dependencies {
    implementation(projects.api)

    implementation(libs.slf4k)
    implementation(libs.logback)

    testImplementation(kotlin("test"))
}

tasks {
    // Relocation is necessary to avoid classpath collision
    shadowJar {
        relocate("kotlin", "dev.kosmx.needle.kotlin")
        relocate("kotlinx", "dev.kosmx.needle.kotlinx")
        relocate("org.slf4j", "dev.kosmx.needle.org.slf4j")
    }
}
