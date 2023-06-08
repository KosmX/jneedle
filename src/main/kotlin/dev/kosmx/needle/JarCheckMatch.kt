package dev.kosmx.needle

data class JarCheckMatch(
    override val status: MatchType,
    val id: String,
    val matchId: String = "",
) : JarCheckResult {
    override fun getMessage() = if (matchId.isEmpty()) "$status($id)" else "$status($id, clue=$matchId)"
}
