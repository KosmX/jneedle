plugins {

    jneedle.compilation
    alias(libs.plugins.kotlin.serialization)

    //alias(libs.plugins.shadow)

    jneedle.repositories
    jneedle.tasks
    jneedle.publishing
    jneedle.dokka
}


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

    implementation(libs.lljzip)

    implementation(libs.slf4j)
    implementation(libs.slf4k)
    "dbGenImplementation"(libs.logback) // API doesn't need a logger backend

    testImplementation(kotlin("test"))
}


tasks {
    withType<Jar>().configureEach {
        manifest {
        }
    }

    // TODO: 2023-06-12 buildDb should be a subproject, or possibly a build script thingy
    create<JavaExec>("buildDb") {
        this.mainClass.set("dev.kosmx.needle.dbGen.GeneratorKt")
        this.classpath = sourceSets["dbGen"].runtimeClasspath
    }
}
