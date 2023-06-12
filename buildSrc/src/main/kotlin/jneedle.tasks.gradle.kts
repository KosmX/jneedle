import org.jetbrains.dokka.gradle.DokkaTask
import java.time.Year
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
                    "Main-Class" to ext["mainClass"],
                    "Implementation-Title" to project.fullName,
                    "Implementation-Version" to project.version.toString(),
                )
            }
        }
    }

    named<DefaultTask>("build") {
        dependsOn(withType<Jar>())
    }
}
