package dev.kosmx.needle.database.serializing

import dev.kosmx.needle.database.asString
import dev.kosmx.needle.matcher.IMatchRule
import dev.kosmx.needle.matcher.impl.legacy.MatchFilteredSequence
import dev.kosmx.needle.matcher.impl.legacy.MatchSequence
import dev.kosmx.needle.matcher.impl.legacy.WildcardMatch
import dev.kosmx.needle.matcher.impl.toWord
import dev.kosmx.needle.matcher.result.Severity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.nio.file.Path
import java.util.jar.JarFile

object LegacyDeserializer {
    fun jasm(path: Path): IMatchRule {

        JarFile(path.toFile()).use { jar ->
            val info =
                Json.decodeFromString<Info>(jar.getInputStream(jar.getJarEntry("info.json")).asString())


            val tree = ClassNode()
            ClassReader(jar.getInputStream(jar.getJarEntry("content.class")).readBytes()).accept(
                tree,
                ClassReader.SKIP_DEBUG
            )

            return if (info.wildcardType) {
                IMatchRule {
                    WildcardMatch(
                        info.name,
                        tree.methods.find { it.name == "pattern" }!!.instructions.toArray(),
                        info.threat,
                        info.matchId,
                        info.filterType,
                    )
                }
            } else if (info.filterType) {
                IMatchRule {
                    MatchFilteredSequence(
                        info.name,
                        tree.methods.find { it.name == "pattern" }!!.instructions.toArray().toWord(),
                        info.threat,
                        matchId = info.matchId
                    )
                }
            } else {
                IMatchRule {
                    MatchSequence(
                        info.name,
                        tree.methods.find { it.name == "pattern" }!!.instructions.toArray(),
                        info.threat,
                        matchId = info.matchId
                    )
                }
            }
        }
    }


    @Serializable
    private data class Info(
        val name: String,
        val threat: Severity = Severity.SEVERE,
        val encoded: Boolean = false,
        val matchId: String = "",
        val filterType: Boolean = false,
        val wildcardType: Boolean = false,
    )
}
