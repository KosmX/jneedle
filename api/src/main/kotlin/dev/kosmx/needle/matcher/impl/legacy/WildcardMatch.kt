package dev.kosmx.needle.matcher.impl.legacy

import dev.kosmx.needle.util.InsnSequence
import dev.kosmx.needle.matcher.IClassMatcher
import dev.kosmx.needle.matcher.IJarMatcher
import dev.kosmx.needle.matcher.impl.filterDebugCodes
import dev.kosmx.needle.matcher.impl.toWord
import dev.kosmx.needle.matcher.result.ClassMatchResult
import dev.kosmx.needle.matcher.result.IScanResult
import dev.kosmx.needle.matcher.result.Severity
import dev.kosmx.needle.util.match
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

class WildcardMatch(
    private val id: String,
    word: Array<AbstractInsnNode>,
    private val matchType: Severity = Severity.SEVERE,
    private val matchId: String = "",
    autoTypeFiltered: Boolean = false,
) : IJarMatcher, IClassMatcher {

    private val typeFilter: Set<Class<out AbstractInsnNode>>? = if (autoTypeFiltered) word.map { it::class.java }.toSet() else null

    private val words: List<InsnSequence>

    var match: IScanResult? = null

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

    private fun match(instructions: Sequence<AbstractInsnNode>): Int {
        val ins = instructions.filterDebugCodes()
            .let {
                if (typeFilter != null) {
                    it.filter { insn -> insn::class.java in typeFilter }
                } else it
            }.iterator()

        for (word in words) {
            if (word.match(ins) == -1) {
                return -1
            }
        }
        return 1
    }
    override fun getClassMatcher(clazz: ClassNode): IClassMatcher = this

    override fun getMatchResult(): IScanResult? = match

    override fun matchMethod(method: MethodNode, instructions: Sequence<AbstractInsnNode>) {
        if (match != null && match(instructions) != -1) {
            match = ClassMatchResult(id, matchId, matchType)
        }
    }
}
