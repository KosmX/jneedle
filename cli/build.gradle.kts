plugins {
    application
    jneedle.compilation
    alias(libs.plugins.kotlin.serialization)


    jneedle.shadow
    jneedle.repositories
    jneedle.tasks
    jneedle.publishing
}

val mainClassName = "dev.kosmx.needle.cli.CliRunKt"
ext["mainClass"] = mainClassName
application.mainClass.set(mainClassName)


dependencies {
    implementation(projects.api)

    implementation(libs.bundles.kotlinx.coroutines)
    testImplementation(libs.bundles.kotlinx.coroutines.debugging)

    implementation(libs.kotlinx.cli)
    implementation(libs.slf4k)
    implementation(libs.logback)

    testImplementation(kotlin("test"))
}

