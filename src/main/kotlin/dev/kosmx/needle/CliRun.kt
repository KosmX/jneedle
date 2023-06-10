package dev.kosmx.needle

import dev.kosmx.needle.core.JarCheckResult
import dev.kosmx.needle.core.JarChecker
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlin.io.path.Path
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {

    val defaultUrl = String(JarCheckResult::class.java.getResourceAsStream("/url")!!.readBytes())

    val parser = ArgParser("jNeedle")

    val file by parser.option(ArgType.String, shortName = "f", fullName = "file", description = "file or directory").required()
    val databaseUrl by parser.option(ArgType.String, shortName = "u", fullName = "url").default(defaultUrl)
    val databaseLocation by parser.option(ArgType.String, fullName = "dblocation").default(CheckWrapper.databasePath.toString())
    val threads by parser.option(ArgType.Int, fullName = "threads").default(Runtime.getRuntime().availableProcessors())

    parser.parse(args)


    CheckWrapper.init(databaseUrl, Path(databaseLocation))


    val path = Path(file)
    if (path.toFile().isFile) {
        println(JarChecker.checkJar(path.toFile()))
    } else {

        measureTime {
            val foundStuff = CheckWrapper.checkPathBlocking(path, threads)


            foundStuff.forEach { (file, founding) ->
                println("$file matches ${founding.map { it.getMessage() }}")
            }

            println("Finished running, found ${foundStuff.size}")
        }.let { println("Malware checking done in ${it.inWholeMilliseconds} ms") }
    }

}


