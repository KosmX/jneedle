package dev.kosmx.needle.scanner

import dev.kosmx.needle.CheckWrapper
import dev.kosmx.needle.CheckWrapper.checkPath
import dev.kosmx.needle.database.Database
import dev.kosmx.needle.matcher.IMatchRule
import dev.kosmx.needle.matcher.result.IScanResult
import dev.kosmx.needle.matcher.result.ScanResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import org.slf4j.kotlin.debug
import org.slf4j.kotlin.error
import org.slf4j.kotlin.getLogger
import software.coley.lljzip.ZipIO
import software.coley.lljzip.format.model.ZipArchive
import java.io.File
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.jvm.internal.Ref

/**
 * Scan config API frontend, scan configuration
 *
 * After constructor is done, the class is immutable and thread-safe
 *
 * @param databaseUrl database server location override
 * @param databaseLocation database cache location override
 * @param rules extra rules to use for checker
 */
class ScanConfig
@JvmOverloads constructor(
    databaseUrl: String? = Defaults.defaultUrl,
    databaseLocation: Path = Defaults.databasePath,
    rules: List<IMatchRule> = listOf(),
) {
    /**
     * Scan rules for config
     */
    val scanRules: List<IMatchRule>
    private val logger by getLogger()

    init {
        databaseLocation.toFile().mkdirs()
        val mutableRules = rules.toMutableList()
        Database.init(mutableRules, databaseUrl, databaseLocation)


        scanRules = mutableRules
    }


    /**
     * Run the check for the given jar file. path has to point to a jar file
     * returns nothing if the file can't be opened as jar
     */
    fun checkJar(path: Path): Set<IScanResult> {
        logger.debug { "Start scanning jar: $path" }
        return try {
            ZipIO.readJvm(path).use { jar ->
                JarScanner.checkJar(this, jar)
            }
        } catch (e: Exception) {
            logger.error(e) {"File $path can't be opened as java archive (jar)"}
            emptySet()
        }
    }

    /**
     * Check jar input stream, can be used from memory files or web API backend
     */
    fun checkJar(zipArchive: ZipArchive): Set<IScanResult> {
        return JarScanner.checkJar(this, zipArchive)
    }


    /**
     * Synchronous (blocking) wrapper for the async [checkPath] function for doing it async
     * If you're from kotlin, please use the async version
     */
    @JvmOverloads
    fun checkPathBlocking(
        path: Path,
        threads: Int = Runtime.getRuntime().availableProcessors() * 4
    ): List<ScanResult> = runBlocking {
        CheckWrapper.checkPath(path, threads)
    }


    /**
     * async checks everything on the path. If the path points to a single file, it will not start threading
     * @param path the path to check, it will recursively walk on the path and check every single file
     * @param threads limit the worker threads. The process can be IO bound, setting it to higher value than the actual CPU cores is normal
     * @param dispatcher dispatcher for file IO checking. The function does the directory walk on the invoker thread.
     * @param jarVisitCallback callback function invoked after every jar check. Invocation happens on thread, it must be thread-safe
     * @return check result
     */
    //@JvmStatic this can only be used from Kotlin, fancy java interface isn't needed
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun checkPath(
        path: Path,
        threads: Int = Runtime.getRuntime().availableProcessors() * 4,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        jarVisitCallback: ((ScanResult) -> Unit) = { },
        scannedCount: Ref.IntRef = Ref.IntRef(),
    ): List<ScanResult> = coroutineScope {
        if (path.toFile().isFile) { // single file selection case
            return@coroutineScope CheckWrapper.checkJar(path).also {
                scannedCount.element++
                jarVisitCallback(path.toFile() to it)
            }.let {
                if (it.isNotEmpty()) {
                    listOf(path.toFile() to it)
                } else listOf()
            }
        }

        val fileChannel = Channel<File>(threads) // fine-tuning may be needed
        val receiveChannel: ReceiveChannel<File> = fileChannel

        val resultChannel = Channel<List<ScanResult>>(threads)

        // Set the fileChannel source. This should effectively endlessly generate files (or as long as there is anything left)
        launch {
            var count = 0
            path.toFile().walk().forEach {
                if (it.extension == "jar" && it.isFile) {
                    count++
                    fileChannel.send(it) // channel has finite size, it will suspend if the buffer is full
                }
            }
            scannedCount.element = count
            fileChannel.close() // all files are sent, "insert close element"
        }

        for (i in 0..<threads) {
            launch(dispatcher) {
                val results = mutableListOf<ScanResult>()

                // for on a channel will receive elements as long as there is any.
                // "if there is a new job, do it."
                for (file in receiveChannel) {
                    val r = checkJar(file.toPath())
                    if (r.isNotEmpty()) {
                        jarVisitCallback(file to r)
                        results += file to r
                    }
                }
                // send back the result
                resultChannel.send(results)
            }
        }


        val results = mutableListOf<ScanResult>()
        for (i in 0..<threads) { // collect the results from all worker threads
            results += resultChannel.receive()
        }
        resultChannel.close() // no more results

        return@coroutineScope results // return
    }

    /**
     * Static configuration helper stuff
     */
    object Defaults {
        val defaultUrl: String = System.getProperty("dev.kosmx.jneedle.remoteDatabase") ?: String(
            ScanConfig::class.java.getResourceAsStream("/url")!!.readBytes()
        )

        val databasePath: Path = System.getProperty("dev.kosmx.jneedle.databasePath")?.let { Path(it) }
            ?: Path(System.getProperty("user.home")).resolve(".cache/jneedle")
    }
}