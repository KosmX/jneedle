package dev.kosmx.needle.cli

import dev.kosmx.needle.CheckWrapper
import dev.kosmx.needle.scanner.ScanConfig
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlinx.coroutines.runBlocking
import org.slf4j.kotlin.debug
import org.slf4j.kotlin.toplevel.getLogger
import kotlin.io.path.Path
import kotlin.jvm.internal.Ref.IntRef
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

// top-level logger, slow!
private val logger by getLogger()
@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {

    val defaultUrl = ScanConfig.Defaults.defaultUrl

    val parser = ArgParser("jNeedle")

    val file by parser.option(ArgType.String, shortName = "f", fullName = "file", description = "file or directory").required()
    val databaseUrl by parser.option(ArgType.String, shortName = "u", fullName = "url").default(defaultUrl)
    val databaseLocation by parser.option(ArgType.String, fullName = "dblocation").default(ScanConfig.Defaults.databasePath.toString())
    val threads by parser.option(ArgType.Int, fullName = "threads").default(Runtime.getRuntime().availableProcessors())

    parser.parse(args)


    CheckWrapper.init(databaseUrl, Path(databaseLocation))

    logger.debug { "Strarting CLI run, file=$file, database=$databaseUrl, local cache=$databaseLocation" }

    val path = Path(file)
    if (path.toFile().isFile) {
        println(CheckWrapper.checkJar(path))
    } else {

        measureTime {
            val count = IntRef()
            val foundStuff = runBlocking { CheckWrapper.checkPath(path, threads, scannedCount = count) }


            foundStuff.forEach { (file, founding) ->
                println("$file matches ${founding.map { it.getMessage() }}")
            }

            println("Finished running, checked ${count.element}, found ${foundStuff.size}")
        }.let { println("Malware checking done in ${it.inWholeMilliseconds} ms") }
    }

}


