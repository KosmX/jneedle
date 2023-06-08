package dev.kosmx.needle.core

import org.objectweb.asm.tree.*

object InsnComparator {

    fun compare(a: AbstractInsnNode, b: AbstractInsnNode): Boolean {
        if (a.opcode != b.opcode) return false
        assert(a::class == b::class)
        return when(a) {
            // field name and owner checking is omitted, it can easily be changed
            is FieldInsnNode -> a.desc == (b as FieldInsnNode).desc
            is FrameNode -> a.type == (b as FieldInsnNode).type // fixme check frame objects
            is IincInsnNode -> if (b is IincInsnNode) a.`var` == b.`var` && a.incr == b.incr else false
            is InsnNode -> true
            is IntInsnNode -> if (b is IntInsnNode) a.operand == b.operand else false
            is InvokeDynamicInsnNode -> if (b is InvokeDynamicInsnNode) a.desc == b.desc && a.bsm.desc == b.bsm.desc else false // omitting name and bsm checks
            is JumpInsnNode -> true // comparing offsets, that should be invariant when injected into code
            is LabelNode -> true
            is LdcInsnNode -> if (b is LdcInsnNode) isAnyOrArrayEqual(a.cst, b.cst) else false
            is LookupSwitchInsnNode -> if (b is LookupSwitchInsnNode) a.keys == b.keys else false // again, not comparing labels
            is MethodInsnNode -> if (b is MethodInsnNode) a.owner == b.owner && a.name == b.name && a.desc == b.desc else false
            is MultiANewArrayInsnNode -> if (b is MultiANewArrayInsnNode) a.desc == b.desc && a.dims == b.dims else false
            is TableSwitchInsnNode -> if (b is TableSwitchInsnNode) a.min == b.min && a.max == b.max else false
            is TypeInsnNode -> if (b is TypeInsnNode) a.desc == b.desc else false
            is VarInsnNode -> if (b is VarInsnNode) a.`var` == b.`var` else false
            else -> true
        }
    }

    private fun isAnyOrArrayEqual(a: Any, b: Any): Boolean {
        return when(a) {
            is Array<*> -> if (b is Array<*>) a.contentEquals(b) else false
            is ByteArray -> if (b is ByteArray) a.contentEquals(b) else false
            is ShortArray -> if (b is ShortArray) a.contentEquals(b) else false
            is IntArray -> if (b is IntArray) a.contentEquals(b) else false
            is LongArray -> if (b is LongArray) a.contentEquals(b) else false
            is FloatArray -> if (b is FloatArray) a.contentEquals(b) else false
            is DoubleArray -> if (b is DoubleArray) a.contentEquals(b) else false
            is BooleanArray -> if (b is BooleanArray) a.contentEquals(b) else false
            else -> a == b
        }
    }
}