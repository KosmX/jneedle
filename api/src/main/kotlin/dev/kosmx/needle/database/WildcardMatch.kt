package dev.kosmx.needle.database

import dev.kosmx.needle.core.InsnComparator
import dev.kosmx.needle.core.JarCheckMatch
import dev.kosmx.needle.core.MatchType
import dev.kosmx.needle.lib.Word
import dev.kosmx.needle.lib.match
import org.objectweb.asm.tree.AbstractInsnNode

class WildcardMatch(
    private val id: String,
    word: Array<AbstractInsnNode>,
    private val matchType: MatchType = MatchType.MALWARE,
    private val matchId: String = "",
    autoTypeFiltered: Boolean = false,
) : ClassMatch {

    private val typeFilter: Set<Class<out AbstractInsnNode>>? = if (autoTypeFiltered) word.map { it::class.java }.toSet() else null

    private val words: List<InsnSequence>

    init {
        val words = mutableListOf<InsnSequence>()
        var current = mutableListOf<AbstractInsnNode>()
        for (insn in word) {
            if (insn.opcode == -1) {
                if (current.isNotEmpty()) {
                    words += current.toWord()
                    current = mutableListOf()
                }
            } else {
                current += insn
            }
        }
        if (current.isNotEmpty()) {
            words += current.toWord()
        }
        this.words = words
    }

    override fun match(instructions: Sequence<AbstractInsnNode>): Int {
        val ins = instructions.filterDebugInfo()
            .let {
                if (typeFilter != null) {
                    it.filter { insn -> insn::class.java in typeFilter }
                } else it
            }.iterator()

        for (word in words) {
            if (word.match(ins) == -1) {
                break
            }
        }
        return 1
    }

    override fun checkResult(): JarCheckMatch = JarCheckMatch(matchType, id, matchId)
}

private fun List<AbstractInsnNode>.toWord(): Word<AbstractInsnNode> {
    return Word(this.toTypedArray(), InsnComparator::compare)
}
