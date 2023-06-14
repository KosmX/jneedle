plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}


tasks {
    shadowJar {
        archiveClassifier.set("")
    }

    jar {
        archiveClassifier.set("slim")
    }
    build {
        dependsOn(shadowJar)
    }
}
