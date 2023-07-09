import kotlin.math.max

plugins {
    java
}

val ext = the<ExtraPropertiesExtension>()
val base = the<BasePluginExtension>()

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()

        failFast = false
        maxParallelForks = max(Runtime.getRuntime().availableProcessors() - 1, 1)
    }

    withType<AbstractArchiveTask>() {
        archiveBaseName.set(project.fullName)
    }

    withType<Javadoc>().configureEach {
        options {
            encoding = "UTF-8"
        }
    }

    withType<Jar>().configureEach {
        from(rootProject.file("LICENSE"))

        doLast {
            manifest {
                attributes(
                    "Implementation-Title" to project.fullName,
                    "Implementation-Version" to project.version.toString(),
                )
                if (ext.has("mainClass")) {
                    attributes(
                        "Main-Class" to ext["mainClass"],
                    )
                }
            }
        }
    }

    named<DefaultTask>("build") {
        dependsOn(withType<Jar>())
    }
}
