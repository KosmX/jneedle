plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}


tasks {
    shadowJar {
        archiveClassifier.set("all")
    }

    jar {
        //archiveClassifier.set("slim")
    }
    build {
        dependsOn(shadowJar)
    }
}
