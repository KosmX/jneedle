package dev.kosmx.needle.database

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LineNumberNode

interface ClassMatch : Match {
    fun match(instructions: Sequence<AbstractInsnNode>): Int

}

fun Sequence<AbstractInsnNode>.filterMutable() = filterDebugInfo()


fun Sequence<AbstractInsnNode>.filterDebugInfo() = filter { it.opcode != -1 }
