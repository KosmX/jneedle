package dev.kosmx.needle.matcher.result

import java.io.File

interface IScanResult {
    val malware: String
    val rule: String
    val severity: Severity
    val extra: String
        get() = ""
}


typealias ScanResult = Pair<File, Set<IScanResult>>
