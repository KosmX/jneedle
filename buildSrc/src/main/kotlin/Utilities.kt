import org.gradle.api.Project
import java.util.*

val Project.fullName: String
    get() = "${rootProject.name}-${project.name}"

fun String.capitalize(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

val Project.isSnapshot: Boolean
    get() = version.toString().endsWith("-SNAPSHOT")

val Project.versionString: String
    get() = project.version as? String ?: project.version.toString()

val Project.groupString: String
    get() = project.group as? String ?: project.group.toString()

object Repository {
    val projectUser = "KosmX"
    val projectRepo = "jneedle"
    val projectBaseUri = "github.com/$projectUser/$projectRepo"
    val projectUrl = "https://$projectBaseUri"
}

/**
 * Project info class for the `processDokkaIncludes` task.
 */
data class ProjectInfo(val group: String, val module: String, val version: String)
