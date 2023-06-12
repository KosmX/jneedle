pluginManagement {
    repositories {
        maven("https://maven.solo-studios.ca/releases/")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
