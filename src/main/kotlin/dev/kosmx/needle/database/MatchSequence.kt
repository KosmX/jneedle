package dev.kosmx.needle.database

import dev.kosmx.needle.JarCheckMatch
import dev.kosmx.needle.JarCheckResult
import dev.kosmx.needle.MatchType
import dev.kosmx.needle.core.InsnComparator
import dev.kosmx.needle.lib.Word
import dev.kosmx.needle.lib.match
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.LabelNode

typealias InsnSequence = Word<AbstractInsnNode>

data class MatchSequence(
    val id: String,
    val word: InsnSequence,
    val matchType: MatchType = MatchType.MALWARE,
    val comparator: (AbstractInsnNode, AbstractInsnNode) -> Boolean = InsnComparator::compare,
    val matchId: String = "",
    ):
    ClassMatch {
    override fun match(instructions: Sequence<AbstractInsnNode>): Int =
        word.match(instructions.filterDebugInstructions().filterLabels())

    constructor(id: String, instructions: Array<AbstractInsnNode>, matchType: MatchType = MatchType.MALWARE, matchId: String = "") :
            this(id, Word(instructions, InsnComparator::compare), matchType, matchId = matchId)

    override fun checkResult(): JarCheckResult = JarCheckMatch(matchType, id, matchId)
}