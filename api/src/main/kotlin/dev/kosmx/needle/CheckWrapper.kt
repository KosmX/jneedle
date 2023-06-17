@file:OptIn(ExperimentalStdlibApi::class)

package dev.kosmx.needle

import dev.kosmx.needle.matcher.result.IScanResult
import dev.kosmx.needle.matcher.result.ScanResult
import dev.kosmx.needle.scanner.ScanConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import software.coley.llzip.format.model.ZipArchive
import java.nio.file.Path
import kotlin.jvm.internal.Ref.IntRef


/**
 * Wrapper (static class) for easier API use
 *
 * First call init(...)
 * Then when it's returned the wrapper state is effectively immutable, calling check functions is thread-safe
 *
 * re-initialization is possible, it will refresh the database (should be thread-safe)
 */
object CheckWrapper {
    private lateinit var scanConfig: ScanConfig


    @JvmStatic
    @JvmOverloads
    fun init(
        databaseUrl: String? = ScanConfig.Defaults.defaultUrl,
        databaseLocation: Path = ScanConfig.Defaults.databasePath,
    ) {
        scanConfig = ScanConfig(databaseUrl, databaseLocation)
    }

    /**
     * Run the check for the given jar file. path has to point to a jar file
     * throws IOException if can't open
     */
    @JvmStatic
    fun checkJar(path: Path): Set<IScanResult> {
        return scanConfig.checkJar(path)
    }

    /**
     * Check jar input stream, can be used from memory files or web API backend
     */
    @JvmStatic
    fun checkJar(zipArchive: ZipArchive): Set<IScanResult> {
        return scanConfig.checkJar(zipArchive)
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
    ): List<ScanResult> = scanConfig.checkPathBlocking(path, threads)

    /**
     * async checks everything on the path. If the path points to a single file, it will not start threading
     * @param path the path to check, it will recursively walk on the path and check every single file
     * @param threads limit the worker threads. The process can be IO bound, setting it to higher value than the actual CPU cores is normal
     * @param dispatcher dispatcher for file IO checking. The function does the directory walk on the invoker thread.
     * @param jarVisitCallback callback function invoked after every jar check. Invocation happens on thread, it must be thread-safe
     * @return check result
     */
    //@JvmStatic this can only be used from Kotlin, fancy java interface isn't needed
    suspend fun checkPath(
        path: Path,
        threads: Int = Runtime.getRuntime().availableProcessors() * 4,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        jarVisitCallback: ((ScanResult) -> Unit) = { },
        scannedCount: IntRef = IntRef(),
    ): List<ScanResult> = scanConfig.checkPath(path, threads, dispatcher, jarVisitCallback, scannedCount)
}

