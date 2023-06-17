package dev.kosmx.needle.matcher.result

import java.io.File

interface IScanResult {
    val malware: String
    val rule: String
    val severity: Severity
    val extra: String
        get() = ""

    fun getMessage(): String =
        if (extra.isEmpty()) "$severity($malware, rule=$rule)" else "$severity($malware, rule=$rule, info=$extra)"
}


typealias ScanResult = Pair<File, Set<IScanResult>>
