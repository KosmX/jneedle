package dev.kosmx.needle.core

import kotlinx.serialization.Serializable

interface JarCheckResult {
    val status: MatchType
    fun getMessage(): String
}

@Serializable
enum class MatchType {
    INFO,
    POTENTIAL,
    MALWARE,
}
