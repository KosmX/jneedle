package dev.kosmx.needle.database

import dev.kosmx.needle.core.MatchType
import org.objectweb.asm.tree.AbstractInsnNode

class MatchFilteredSequence
    (
    id: String,
    word: InsnSequence,
    matchType: MatchType = MatchType.MALWARE,
    matchId: String = "",
    private val typeFilter: Set<Class<out AbstractInsnNode>> = word.word.map { it::class.java }.toSet()
            )
    : MatchSequence(id, word, matchType, matchId) {
    override fun match(instructions: Sequence<AbstractInsnNode>): Int {
        return super.match(instructions.filter {insn -> insn::class.java in typeFilter})
    }
}