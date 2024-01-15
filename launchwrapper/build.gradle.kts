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
        isEnableRelocation = true
        relocationPrefix = "needleWrapper"
        exclude("META-INF/**")
    }

    jar {
        manifest {
            attributes(
                "Premain-Class" to "needleWrapper.dev.kosmx.needle.launchWrapper.JavaAgentLauncher",
                "Agent-Class" to "needleWrapper.dev.kosmx.needle.launchWrapper.JavaAgentLauncher",)
        }
    }
}
