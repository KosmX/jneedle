package dev.kosmx.needle

import dev.kosmx.needle.core.JarChecker
import dev.kosmx.needle.database.Database
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlinx.coroutines.*
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.Path
import kotlin.io.path.extension

fun main(args: Array<String>) {

    val defaultUrl = String(JarCheckResult::class.java.getResourceAsStream("/url")!!.readAllBytes())

    val parser = ArgParser("jar checker")
    val file by parser.option(ArgType.String, shortName = "f", fullName = "file", description = "file or directory").required()
    val databaseUrl by parser.option(ArgType.String, shortName = "u", fullName = "url").default(defaultUrl)
    val databaseLocation by parser.option(ArgType.String, fullName = "dblocation").default(Path(System.getProperty("user.home")).resolve(".jneedle").toString())
    val threads by parser.option(ArgType.Int, fullName = "threads").default(Runtime.getRuntime().availableProcessors())

    parser.parse(args)


    val db = Path(databaseLocation)
    if (!db.toFile().isDirectory) {
        db.toFile().mkdirs()
    }
    Database.init(databaseUrl, db)

    val path = Path(file)
    if (path.toFile().isFile) {
        println(JarChecker.checkJar(path.toFile()))
    } else {

        val dispatcher = Executors.newFixedThreadPool(threads).asCoroutineDispatcher()

        val counter = AtomicInteger(0)
        val foundStuff = AtomicInteger(0)
        Files.walkFileTree(
            path,
            setOf(FileVisitOption.FOLLOW_LINKS),
            Int.MAX_VALUE,
            object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    if (file.extension == "jar") {
                        counter.incrementAndGet()
                        CoroutineScope(dispatcher).launch {
                            val result = JarChecker.checkJar(file.toFile())
                            if (result.isNotEmpty()) {
                                foundStuff.incrementAndGet()
                                withContext(Dispatchers.Default) {
                                    println("$file matches ${result.map { it.getMessage() }}")
                                    counter.decrementAndGet()
                                }
                            } else {
                                counter.decrementAndGet()
                            }
                        }
                    }

                    return super.visitFile(file, attrs)
                }
            }
        )

        dispatcher.close()
        println("Finished running, found ${foundStuff.get()}")
    }

}


