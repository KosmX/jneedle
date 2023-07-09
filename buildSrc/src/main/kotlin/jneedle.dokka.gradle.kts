import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.dokka.gradle.DokkaTask
import java.time.Year

plugins {
    id("org.jetbrains.dokka")
}

tasks {
    val processDokkaIncludes by tasks.register("processDokkaIncludes", ProcessResources::class) {
        from(rootProject.projectDir.resolve("dokka/includes")) {
            val projectInfo = ProjectInfo(project.groupString, project.fullName, project.versionString)
            expand(
                "project" to projectInfo,
            )
        }
        destinationDir = buildDir.resolve("dokka-include")
        group = JavaBasePlugin.DOCUMENTATION_GROUP
    }

    withType<DokkaTask>().configureEach {
        dependsOn(processDokkaIncludes)

        val dokkaBaseConfiguration = """
            {
                "footerMessage": "© ${Year.now()} Copyright jNeedle Contributors",
                "separateInheritedMembers": true
            }
        """.trimIndent()
        pluginsMapConfiguration.set(mapOf("org.jetbrains.dokka.base.DokkaBase" to dokkaBaseConfiguration))

        moduleName.set(project.fullName)
        moduleVersion.set(project.versionString)

        dokkaSourceSets.configureEach {
            includes.from(processDokkaIncludes.destinationDir.listFiles())

            jdkVersion.set(8)
            reportUndocumented.set(true)

            // displayName.set(project.fullName)
        }

        group = JavaBasePlugin.DOCUMENTATION_GROUP
    }
}
