package dev.kosmx.needle.scanner

import dev.kosmx.needle.matcher.FileMatchContext
import dev.kosmx.needle.matcher.result.IScanResult
import org.slf4j.kotlin.debug
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info
import org.slf4j.kotlin.warn
import software.coley.llzip.ZipIO
import software.coley.llzip.format.compression.ZipCompressions
import software.coley.llzip.format.model.LocalFileHeader
import software.coley.llzip.format.model.ZipArchive
import software.coley.llzip.util.ByteDataUtil
import java.io.IOException


object JarScanner {
    private val logger by getLogger()

    fun checkJar(config: ScanConfig, jar: ZipArchive): Set<IScanResult> {
        val results = mutableSetOf<IScanResult>()
        val matches = config.scanRules.mapNotNull { it.getJarMatcher(FileMatchContext(config, jar)) }


        zipEntry@for (jarEntry in jar) {
            val byteData by lazy { ZipCompressions.decompress(jarEntry) }
            val bytes = lazy { ByteDataUtil.toByteArray(byteData)!! }
            if (jarEntry.fileNameAsString.let { it.endsWith(".class") || it.endsWith(".class/") }) {
                try {
                    ClassScanner.checkClass(bytes.value, matches)
                    continue@zipEntry
                } catch (e: IOException) {
                    logger.warn(e) { "Failed to scan class file: ${jarEntry.fileNameAsString}" }
                    //results += JarCheckMatch(MatchType.POTENTIAL, "Illegal .class file: ${jarEntry.name}")
                }
            } else if (jarEntry.fileNameAsString.let { it.endsWith(".jar") || it.endsWith(".jar/") }) {
                try {
                    logger.debug { "Start scanning nested jar: ${jarEntry.fileNameAsString}" }
                    ZipIO.readJvm(byteData).use { nestedJar ->
                        results += checkJar(config, nestedJar)
                    }
                    continue@zipEntry
                } catch (t: Throwable) {
                    //TODO results += JarCheckMatch(MatchType.INFO, "Failed to open nested jar: ${t.message}")
                    logger.info(t) {
                        t.message.orEmpty()
                    }
                }
            }

            try {
                matches.forEach { it.matchAsset(jarEntry, bytes) }
            } catch (t: Throwable) {
                //TODO results += JarCheckMatch(MatchType.INFO, "Asset checker failed: ${t.message}")
                logger.warn(t) { t.message.orEmpty() }
            }

        } // end zipEntry

        // aand finally collect the results
        results += matches.mapNotNull { it.getMatchResult() }

        return results
    }
}

fun ZipArchive.asSequence() = localFiles.asSequence().map { it }

operator fun ZipArchive.iterator(): Iterator<LocalFileHeader> = this.asSequence().iterator()

