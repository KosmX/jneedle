package dev.kosmx.needle.matcher.impl

import dev.kosmx.needle.util.InsnComparator
import dev.kosmx.needle.util.Word
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodNode


internal fun Array<AbstractInsnNode>.toWord(): Word<AbstractInsnNode> {
    return Word(this, InsnComparator()::compare)
}

internal fun List<AbstractInsnNode>.toWord(): Word<AbstractInsnNode> {
    return Word(this.toTypedArray(), InsnComparator()::compare)
}


fun Sequence<AbstractInsnNode>.filterDebugCodes() = filter { it.opcode != -1 }

val MethodNode.id: String
    get() = "$name$desc"
