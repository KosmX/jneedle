package dev.kosmx.needle.database

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LineNumberNode

interface ClassMatch : Match {
    fun match(instructions: Sequence<AbstractInsnNode>): Int

}

fun Sequence<AbstractInsnNode>.filterDebugInstructions() = filter {
    it !is LineNumberNode
}
fun Sequence<AbstractInsnNode>.filterLabels() = filter { it !is LabelNode }
