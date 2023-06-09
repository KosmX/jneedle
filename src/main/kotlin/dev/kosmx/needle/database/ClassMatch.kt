package dev.kosmx.needle.database

import dev.kosmx.needle.core.JarCheckMatch
import org.objectweb.asm.tree.AbstractInsnNode

interface ClassMatch : Match {
    fun match(instructions: Sequence<AbstractInsnNode>): Int

    override fun checkResult(): JarCheckMatch
}

fun Sequence<AbstractInsnNode>.filterMutable() = filterDebugInfo()


fun Sequence<AbstractInsnNode>.filterDebugInfo() = filter { it.opcode != -1 }
