package dev.kosmx.needle.matcher.result

interface IScanResult {
    val malware: String
    val rule: String
    val severity: Severity
    val extra: String
        get() = ""
}