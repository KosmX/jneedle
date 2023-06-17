package dev.kosmx.needle.database

import dev.kosmx.needle.util.InsnComparator
import dev.kosmx.needle.core.JarCheckMatch
import dev.kosmx.needle.core.JarCheckResult
import dev.kosmx.needle.core.MatchType
import dev.kosmx.needle.util.Word
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.slf4j.kotlin.error
import org.slf4j.kotlin.getLogger
import software.coley.llzip.format.model.LocalFileHeader
import java.nio.file.Path
import java.util.*
import java.util.jar.JarFile
import kotlin.io.path.extension

object FileParser {
    private val logger by getLogger()

    @OptIn(ExperimentalSerializationApi::class)
    fun parseFile(path: Path): Match? {
        try {
            return when (path.extension) {
                "jasm" -> {
                    JarFile(path.toFile()).use { jar ->
                        val info = Json.decodeFromString<Info>(jar.getInputStream(jar.getJarEntry("info.json")).asString())


                        val tree = ClassNode()
                        ClassReader(jar.getInputStream(jar.getJarEntry("content.class")).readBytes()).accept(
                            tree,
                            ClassReader.SKIP_DEBUG
                        )

                        if (info.wildcardType) {
                            WildcardMatch(
                                info.name,
                                tree.methods.find { it.name == "pattern" }!!.instructions.toArray(),
                                info.threat,
                                info.matchId,
                                info.filterType,
                                )
                        } else if (info.filterType) {
                            MatchFilteredSequence (
                                info.name,
                                tree.methods.find { it.name == "pattern" }!!.instructions.toArray().toWord(),
                                info.threat,
                                matchId = info.matchId
                            )
                        } else {
                            MatchSequence(
                                info.name,
                                tree.methods.find { it.name == "pattern" }!!.instructions.toArray(),
                                info.threat,
                                matchId = info.matchId
                            )
                        }
                    }
                }

                "asset" -> {
                    path.toFile().inputStream().use {
                        Json.decodeFromString<Asset>(it.asString())
                    }
                }

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

private fun Array<AbstractInsnNode>.toWord(): Word<AbstractInsnNode> {
    return Word(this, InsnComparator()::compare)
}

@Serializable
data class Info(
    val name: String,
    val threat: MatchType = MatchType.MALWARE,
    val encoded: Boolean = false,
    val matchId: String = "",
    val filterType: Boolean = false,
    val wildcardType: Boolean = false,
)



@Serializable
data class Asset(val name: String, val threat: MatchType = MatchType.POTENTIAL, val data: String? = null, val fileName: String? = null) :
    AssetMatch {
    private val bytes by lazy { data?.let { Base64.getDecoder().decode(it) } }
    override fun match(data: Lazy<ByteArray>, jarEntry: LocalFileHeader): Boolean {
        if (fileName != null && jarEntry.fileNameAsString != name) return false

        return if (bytes != null) {
            data.value.contentEquals(bytes)
        } else {
            true
        }
    }

    override fun checkResult(): JarCheckResult = JarCheckMatch(threat, name)

}
