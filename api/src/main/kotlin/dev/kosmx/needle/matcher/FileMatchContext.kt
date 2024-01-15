package dev.kosmx.needle.matcher

import dev.kosmx.needle.scanner.ScanConfig
import software.coley.lljzip.format.model.ZipArchive


/**
 * Context for file matching
 */
data class FileMatchContext(
    /**
     * Current scan configuration
     */
    val config: ScanConfig,
    /**
     * zipArchive of the jar. DO NOT CLOSE
     */
    val jar: ZipArchive,
    /**
     * efficient string matcher, string equality should be delegated for efficiency
     */
    // val stringMatcher
)
