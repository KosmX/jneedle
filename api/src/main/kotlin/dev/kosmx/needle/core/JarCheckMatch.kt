package dev.kosmx.needle.core

data class JarCheckMatch(
    override val status: MatchType,
    val id: String,
    val matchId: String = "",
    val node: String? = null,
    val method: String? = null,
) : JarCheckResult {
    override fun getMessage() = if (matchId.isEmpty()) "$status($id)" else "$status($id, clue=$matchId)"
}
