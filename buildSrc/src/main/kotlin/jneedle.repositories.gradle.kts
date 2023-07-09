repositories {
    mavenCentral()

    google()

    maven("https://jitpack.io") {
        name = "Jitpack"
    }

    maven("https://oss.sonatype.org/content/repositories/snapshots") {
        name = "Maven Snapshots"
        mavenContent {
            snapshotsOnly()
        }
    }
}
