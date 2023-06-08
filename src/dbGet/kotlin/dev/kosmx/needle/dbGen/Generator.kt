package dev.kosmx.needle.dbGen

import dev.kosmx.needle.database.DatabaseFiles
import dev.kosmx.needle.dbGen.db.Fractureiser
import dev.kosmx.needle.dbGen.db.Fractureiser.entry
import dev.kosmx.needle.dbGen.dsl.Entry
import dev.kosmx.needle.dbGen.dsl.KDSL
import dev.kosmx.needle.dbGen.dsl.TypeEntry
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import kotlin.io.path.Path
import kotlin.io.path.outputStream
import kotlin.io.path.writeText


val databaseId = 1
val entries = listOf<TypeEntry>(

    Fractureiser,

)



@OptIn(ExperimentalSerializationApi::class)
fun main() {

    val path = Path("database")
    path.toFile().let {
        it.deleteRecursively()
        it.mkdirs()
    }
    path.resolve("version").writeText(databaseId.toString())
    val dataPath = path.resolve("data")
    dataPath.toFile().mkdirs()

    val names = entries.flatMap {
        val list = mutableListOf<Entry>()
        KDSL(list).also { dsl -> dsl.entry() }
        list
    }.map {
        it.generate(dataPath)
    }.toList()

    path.resolve("files.json").outputStream().use {
        Json.encodeToStream(DatabaseFiles(names), it)
    }

}
