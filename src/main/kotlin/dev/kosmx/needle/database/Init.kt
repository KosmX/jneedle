package dev.kosmx.needle.database

import dev.kosmx.needle.LogLevel
import dev.kosmx.needle.core.AssetChecker
import dev.kosmx.needle.core.ClassChecker
import dev.kosmx.needle.log
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.streams.asSequence

/**
 * Initialize database, get ready for checking
 */
object Database {

    var database: List<Match> = listOf()
        get
        private set


    fun init(databaseUrl: String?, dataPath: Path) {
        try {
            if (databaseUrl != null) {
                val localDatabase = dataPath.resolve("database")
                var localDbVersion = -1
                try {
                    localDbVersion = localDatabase.resolve("version").readText().toInt()
                } catch (_: IOException) {
                }

                val databaseVersion = URL("$databaseUrl/version").readText().toInt()
                if (localDbVersion < databaseVersion) {
                    dataPath.resolve("data").toFile().let { if (!it.isDirectory) it.mkdir() }
                    updateDb(databaseUrl, dataPath.resolve("data"))
                    localDatabase.resolve("version").writeText(databaseVersion.toString())
                }
            }
        } catch (e: Exception) {
            log(LogLevel.Warning) { "Failed to fetch database: $databaseUrl is not available."}
        }

        if (dataPath.toFile().isDirectory) {

            database = Files.walk(dataPath.resolve("data")).asSequence().mapNotNull {
                FileParser.parseFile(it)
            }.toList()
        } else {
            TODO("How to list resources form the jar file?!")
        }

        ClassChecker.init(database.filterIsInstance<ClassMatch>())
        AssetChecker.init(database.filterIsInstance<AssetMatch>())
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun updateDb(databaseUrl: String, dataPath: Path) {
        val files = URL("$databaseUrl/files.json").openConnection().getInputStream().use {
            Json.decodeFromStream<DatabaseFiles>(it)
        }
        Files.walk(dataPath).filter { it.name !in files.files }.forEach {
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

@Serializable
data class DatabaseFiles(val files: List<String>)

