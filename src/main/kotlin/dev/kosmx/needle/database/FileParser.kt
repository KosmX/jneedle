package dev.kosmx.needle.database

import dev.kosmx.needle.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.nio.file.Path
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.io.path.extension

object FileParser {
    @OptIn(ExperimentalSerializationApi::class)
    fun parseFile(path: Path): Match? {
        try {
            return when (path.extension) {
                "jasm" -> {
                    JarFile(path.toFile()).use { jar ->
                        val info = Json.decodeFromStream<Info>(jar.getInputStream(jar.getJarEntry("info.json")))


                        val tree = ClassNode()
                        ClassReader(jar.getInputStream(jar.getJarEntry("content.class")).readAllBytes()).accept(
                            tree,
                            ClassReader.SKIP_DEBUG
                        )

                        MatchSequence(info.name, tree.methods.find { it.name == "pattern" }!!.instructions.toArray(), info.threat, matchId = info.matchId)
                    }
                }

                "asset" -> {
                    path.toFile().inputStream().use {
                        Json.decodeFromStream<Asset>(it)
                    }
                }

                else -> null
            }
        } catch (e: Exception) {
            log(LogLevel.Error) {
                e.printStackTrace()
                "Failed to load database entry: $path reason: ${e.message}"
            }
            return null
        }
    }
}

@Serializable
data class Info(val name: String, val threat: MatchType = MatchType.MALWARE, val encoded: Boolean = false, val matchId: String = "")



@Serializable
data class Asset(val name: String, val threat: MatchType = MatchType.POTENTIAL, val data: String? = null, val fileName: String? = null) :
    AssetMatch {
    private val bytes by lazy { data?.let { Base64.getDecoder().decode(it) } }
    override fun match(data: Lazy<ByteArray>, jarEntry: JarEntry): Boolean {
        if (fileName != null && jarEntry.name != name) return false

        return if (bytes != null) {
            data.value.contentEquals(bytes)
        } else {
            true
        }
    }

    override fun checkResult(): JarCheckResult = JarCheckMatch(threat, name)

}
