package dev.kosmx.needle.matcher.impl.legacy

import dev.kosmx.needle.util.InsnSequence
import dev.kosmx.needle.matcher.result.Severity
import org.objectweb.asm.tree.AbstractInsnNode

class MatchFilteredSequence
    (
    id: String,
    word: InsnSequence,
    matchType: Severity = Severity.SEVERE,
    matchId: String = "",
    private val typeFilter: Set<Class<out AbstractInsnNode>> = word.word.map { it::class.java }.toSet()
            )
    : MatchSequence(id, word, matchType, matchId) {
    override fun match(instructions: Sequence<AbstractInsnNode>): Int {
        return super.match(instructions.filter {insn -> insn::class.java in typeFilter})
    }
}