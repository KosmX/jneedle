import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    java

    kotlin("jvm")

    id("org.jetbrains.dokka")
}

java {
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(8)
}

val dokkaHtml by tasks.getting(DokkaTask::class)

val javadoc by tasks.getting(Javadoc::class)

val jar by tasks.getting(Jar::class)

val javadocJar by tasks.getting(Jar::class) {
    dependsOn(dokkaHtml)
    from(dokkaHtml.outputDirectory)
    archiveClassifier.set("javadoc")
    group = JavaBasePlugin.DOCUMENTATION_GROUP
}

val sourcesJar by tasks.getting(Jar::class) {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
    group = JavaBasePlugin.DOCUMENTATION_GROUP
}

artifacts {
    archives(jar)
    archives(sourcesJar)
    archives(javadocJar)
}
