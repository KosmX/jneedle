package dev.kosmx.needle

import dev.kosmx.needle.core.JarCheckResult
import dev.kosmx.needle.core.JarChecker
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlin.io.path.Path

fun main(args: Array<String>) {

    val defaultUrl = String(JarCheckResult::class.java.getResourceAsStream("/url")!!.readAllBytes())

    val parser = ArgParser("jar checker")
    val file by parser.option(ArgType.String, shortName = "f", fullName = "file", description = "file or directory").required()
    val databaseUrl by parser.option(ArgType.String, shortName = "u", fullName = "url").default(defaultUrl)
    val databaseLocation by parser.option(ArgType.String, fullName = "dblocation").default(Path(System.getProperty("user.home")).resolve(".jneedle").toString())
    val threads by parser.option(ArgType.Int, fullName = "threads").default(Runtime.getRuntime().availableProcessors())

    parser.parse(args)


    CheckWrapper.init(databaseUrl, databaseLocation)


    val path = Path(file)
    if (path.toFile().isFile) {
        println(JarChecker.checkJar(path.toFile()))
    } else {

        val foundStuff = CheckWrapper.checkPathBlocking(path)

        foundStuff.forEach {(file, founding) ->
            println("$file matches ${founding.map { it.getMessage() }}")
        }

        println("Finished running, found ${foundStuff.size}")
        /**
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

        */
    }

}


