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
import org.slf4j.kotlin.error
import org.slf4j.kotlin.getLogger
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.io.path.extension

object FileParser {
    private val logger by getLogger()

    fun parseFile(path: Path): IMatchRule? {
        try {
            return when (path.extension) {
                "jasm" -> {
                    JarFile(path.toFile()).use { jar ->
                        val info =
                            Json.decodeFromString<Info>(jar.getInputStream(jar.getJarEntry("info.json")).asString())


                        val tree = ClassNode()
                        ClassReader(jar.getInputStream(jar.getJarEntry("content.class")).readBytes()).accept(
                            tree,
                            ClassReader.SKIP_DEBUG
                        )

                        if (info.wildcardType) {
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

                "asset" -> TODO()

                else -> null
            }
        } catch (e: Exception) {
            logger.error {
                e.printStackTrace()
                "Failed to load database entry: $path reason: ${e.message}"
            }
            return null
        }
    }
}


@Serializable
data class Info(
    val name: String,
    val threat: Severity = Severity.SEVERE,
    val encoded: Boolean = false,
    val matchId: String = "",
    val filterType: Boolean = false,
    val wildcardType: Boolean = false,
)


