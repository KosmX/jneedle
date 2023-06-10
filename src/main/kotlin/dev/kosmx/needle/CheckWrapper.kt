@file:OptIn(ExperimentalStdlibApi::class)

package dev.kosmx.needle

import dev.kosmx.needle.core.JarCheckResult
import dev.kosmx.needle.core.JarChecker
import dev.kosmx.needle.database.Database
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.util.jar.JarInputStream
import kotlin.io.path.Path
import kotlin.jvm.internal.Ref.IntRef


typealias ScanResult = Pair<File, Set<JarCheckResult>>

/**
 * Wrapper (static class) for easier API use
 *
 * First call init(...)
 * Then when it's returned the program state is effectively immutable, calling check functions is thread-safe
 *
 * re-initialization is possible, it will refresh the database (should be thread-safe, but I wouldn't bet on it)
 */
object CheckWrapper {
    private var initialized = false

    private val defaultUrl = System.getProperty("dev.kosmx.jneedle.remoteDatabase") ?: String(
        JarCheckResult::class.java.getResourceAsStream("/url")!!.readBytes()
    )

    @JvmStatic
    @JvmOverloads
    fun init(
        databaseUrl: String? = defaultUrl,
        databaseLocation: Path = databasePath
    ) {

        databaseLocation.toFile().mkdirs()
        Database.init(databaseUrl, databaseLocation)
        initialized = true
    }

    val databasePath: Path
        get() = System.getProperty("dev.kosmx.jneedle.databasePath")?.let { Path(it) }
            ?: Path(System.getProperty("user.home")).resolve(".jneedle")

    /**
     * Run the check for the given jar file. path has to point to a jar file
     * throws IOException if can't open
     */
    @JvmStatic
    fun checkJar(path: Path): Set<JarCheckResult> {
        require(initialized) { "Cannot run check before initialization is complete" }

        path.toFile().inputStream().use {
            return checkJar(it)
        }
    }

    /**
     * Check jar input stream, can be used from memory files or web API backend
     */
    @JvmStatic
    fun checkJar(inputStream: InputStream): Set<JarCheckResult> {
        require(initialized) { "Cannot run check before initialization is complete" }

        JarInputStream(inputStream).use {
            return JarChecker.checkJar(it)
        }
    }

    /**
     * Synchronous (blocking) wrapper for the async [checkPath] function for doing it async
     * If you're from kotlin, please use the async version
     */
    @JvmStatic
    @JvmOverloads
    fun checkPathBlocking(
        path: Path,
        threads: Int = Runtime.getRuntime().availableProcessors() * 4
    ): List<ScanResult> = runBlocking {
        checkPath(path, threads)
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
        scannedCount: IntRef = IntRef(),
    ): List<ScanResult> = coroutineScope {
        require(initialized) { "Cannot run check before initialization is complete" }
        if (path.toFile().isFile) { // single file selection case
            return@coroutineScope listOf(path.toFile() to checkJar(path).also {
                scannedCount.element++
                jarVisitCallback(path.toFile() to it)
            })
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
                    val r = JarChecker.checkJar(file)
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
}

