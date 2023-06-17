package dev.kosmx.needle.matcher.impl.legacy

import dev.kosmx.needle.util.InsnSequence
import dev.kosmx.needle.matcher.IClassMatcher
import dev.kosmx.needle.matcher.IJarMatcher
import dev.kosmx.needle.matcher.impl.filterDebugCodes
import dev.kosmx.needle.matcher.impl.id
import dev.kosmx.needle.matcher.result.ClassMatchResult
import dev.kosmx.needle.matcher.result.ClueMatchResult
import dev.kosmx.needle.matcher.result.IScanResult
import dev.kosmx.needle.matcher.result.Severity
import dev.kosmx.needle.util.InsnComparator
import dev.kosmx.needle.util.Word
import dev.kosmx.needle.util.match
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

open class MatchSequence(
    private val id: String,
    private val word: InsnSequence,
    private val matchType: Severity = Severity.SEVERE,
    private val matchId: String = "",
) : IJarMatcher, IClassMatcher {

    private var match: IScanResult? = null
    lateinit var lastClass: ClassNode

    protected open fun match(instructions: Sequence<AbstractInsnNode>): Int =
        word.match(instructions.filterDebugCodes())


    constructor(
        id: String,
        instructions: Array<AbstractInsnNode>,
        matchType: Severity = Severity.SEVERE,
        matchId: String = ""
    ) :
            this(id, Word(instructions, InsnComparator()::compare), matchType, matchId = matchId)

    override fun getClassMatcher(clazz: ClassNode): IClassMatcher {
        lastClass = clazz
        return this
    }

    override fun getMatchResult() = match

    override fun matchMethod(method: MethodNode, instructions: Sequence<AbstractInsnNode>) {
        if (match == null && match(instructions) != -1) {
            match = ClassMatchResult(id, matchId, matchType, ClueMatchResult(matchId, lastClass.name, method.id))
        }
    }
}