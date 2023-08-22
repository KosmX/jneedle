package dev.kosmx.needle.dbGen

import dev.kosmx.needle.database.DatabaseFiles
import dev.kosmx.needle.dbGen.dsl.Entry
import dev.kosmx.needle.dbGen.dsl.KDSL
import dev.kosmx.needle.dbGen.dsl.TypeEntry
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.io.path.Path
import kotlin.io.path.outputStream
import kotlin.io.path.writeText
import kotlin.streams.asSequence


const val databaseId = 6




val entries = mutableListOf<TypeEntry>()

fun listEntries() {

    fun getClass(className: String, packageName: String): Class<*>? {
        try {
            return Class.forName(
                packageName + "."
                        + className.substring(0, className.lastIndexOf('.'))
            )
        } catch (e: ClassNotFoundException) {
            // handle the exception
        }
        return null
    }

    val stream = ClassLoader.getSystemClassLoader()
        .getResourceAsStream("dev/kosmx/needle/dbGen/db")!!
    val reader = BufferedReader(InputStreamReader(stream))
    val classes = reader.lines().asSequence()
        .filter { line: String -> line.endsWith(".class") }
        .mapNotNull { line: String -> getClass(line, "dev.kosmx.needle.dbGen.db") }
        .toSet()

    classes.mapNotNull { clazz ->
        try {
            clazz.methods.find { it.parameterTypes.contentEquals(arrayOf(KDSL::class.java)) }
        } catch (e: Exception) { null }
    }.forEach { method ->
        entries += TypeEntry {
            method.invoke(null, it)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    listEntries()

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
        KDSL(list).also { dsl -> it.entry(dsl) }.also {
            println("Processed ${it.malwareId}")
        }
        list
    }.map {
        it.generate(dataPath)
    }.toList()

    path.resolve("files.json").outputStream().use {
        Json.encodeToStream(DatabaseFiles(names), it)
    }

}
