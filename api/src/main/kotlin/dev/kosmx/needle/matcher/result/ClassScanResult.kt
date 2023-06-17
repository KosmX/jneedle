package dev.kosmx.needle.matcher.result

data class ClassMatchResult(
    override val malware: String,
    override val rule: String,
    override val severity: Severity,
    val clues: Set<ClueMatchResult>,
) : IScanResult {

    constructor(malware: String, rule: String, severity: Severity, vararg clues: ClueMatchResult) : this(malware, rule, severity, clues.toSet())

    override val extra: String
        get() = if (clues.isNotEmpty()) clues.toString() else ""
}


data class ClueMatchResult(
    val clue: String,
    val `class`: String,
    val method: String,
)
