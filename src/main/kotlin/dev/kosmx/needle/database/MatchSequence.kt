package dev.kosmx.needle.database

import dev.kosmx.needle.JarCheckMatch
import dev.kosmx.needle.JarCheckResult
import dev.kosmx.needle.MatchType
import dev.kosmx.needle.core.InsnComparator
import dev.kosmx.needle.lib.Word
import dev.kosmx.needle.lib.match
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.LabelNode
import kotlin.reflect.KClass

typealias InsnSequence = Word<AbstractInsnNode>

open class MatchSequence(
    private val id: String,
    private val word: InsnSequence,
    private val matchType: MatchType = MatchType.MALWARE,
    private val matchId: String = "",
    ):
    ClassMatch {
    open override fun match(instructions: Sequence<AbstractInsnNode>): Int =
        word.match(instructions.filterMutable())

    constructor(id: String, instructions: Array<AbstractInsnNode>, matchType: MatchType = MatchType.MALWARE, matchId: String = "") :
            this(id, Word(instructions, InsnComparator::compare), matchType, matchId = matchId)

    override fun checkResult(): JarCheckResult = JarCheckMatch(matchType, id, matchId)
}