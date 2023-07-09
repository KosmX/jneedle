package dev.kosmx.needle.database

import dev.kosmx.needle.database.hardCodedDetectors.HardCodedDetectors
import dev.kosmx.needle.database.serializing.FileParser
import dev.kosmx.needle.matcher.IMatchRule
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.warn
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.streams.asSequence

/**
 * Initialize database, get ready for checking
 */
object Database {
    private val logger by getLogger()


    fun init(database: MutableList<IMatchRule>, databaseUrl: String?, dataPath: Path) {
        try {
            if (databaseUrl != null) {
                var localDbVersion = -1
                try {
                    localDbVersion = dataPath.resolve("version").readText().toInt()
                } catch (_: IOException) {
                }

                val databaseVersion = URL("$databaseUrl/version").readText().toInt()
                if (localDbVersion < databaseVersion) {
                    dataPath.resolve("data").toFile().let { if (!it.isDirectory) it.mkdir() }
                    updateDb(databaseUrl, dataPath.resolve("data"))
                    dataPath.resolve("version").writeText(databaseVersion.toString())
                }
            }
        } catch (e: Exception) {
            logger.warn(e) {
                "Failed to fetch database: $databaseUrl is not available."
            }
        }

        if (dataPath.toFile().isDirectory) {

            database += Files.walk(dataPath.resolve("data")).asSequence().mapNotNull {
                FileParser.parseFile(it)
            }.toList()
        } else {
            TODO("How to list resources form the jar file?!")
        }
        database += HardCodedDetectors.getHardCodedDetectors()

    }

    private fun updateDb(databaseUrl: String, dataPath: Path) {
        val files = URL("$databaseUrl/files.json").openConnection().getInputStream().use {
            Json.decodeFromString<DatabaseFiles>(it.asString())
        }
        Files.walk(dataPath).filter { it.toFile().isFile && it.name !in files.files }.forEach {
            Files.delete(it) // delete outdated things
        }

        val toDownload = files.files.filter { !dataPath.resolve(it).toFile().isFile }

        runBlocking {
            withContext(Dispatchers.IO) {
                val deferreds = toDownload.map { file ->
                    async {
                        URL("$databaseUrl/data/$file").openConnection().getInputStream().use { input ->
                            Files.copy(input, dataPath.resolve(file))
                        }
                    }
                }
                deferreds.awaitAll()
            }
        }
    }
}

fun InputStream.asString(): String = String(readBytes())

@Serializable
data class DatabaseFiles(val files: List<String>)

