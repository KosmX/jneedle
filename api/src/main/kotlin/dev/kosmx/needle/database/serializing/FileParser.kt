package dev.kosmx.needle.database.serializing

import dev.kosmx.needle.matcher.IMatchRule
import org.slf4j.kotlin.error
import org.slf4j.kotlin.getLogger
import java.nio.file.Path
import kotlin.io.path.extension

object FileParser {
    private val logger by getLogger()

    fun parseFile(path: Path): IMatchRule? {
        return try {
            when (path.extension) {
                "jasm" -> LegacyDeserializer.jasm(path)

                "asset" -> TODO()

                else -> null
            }
        } catch (e: Exception) {
            logger.error(e) {
                "Failed to load database entry: $path reason: ${e.message}"
            }
            null
        }
    }
}




