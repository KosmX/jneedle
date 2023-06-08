package dev.kosmx.needle.core

import dev.kosmx.needle.JarCheckResult
import dev.kosmx.needle.database.ClassMatch
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LineNumberNode


object ClassChecker {

    fun init(p: List<ClassMatch>) {
        sequences = p
    }

    private lateinit var sequences: List<ClassMatch>


    fun checkClass(classReader: ClassReader): Set<JarCheckResult> {
        val result = mutableSetOf<JarCheckResult>()

        val node = ClassNode()
        classReader.accept(node, ClassReader.SKIP_DEBUG)

        node.methods.forEach { method ->
            for (word in sequences) {
                if (word.match(method.instructions.asSequence()) != -1) {
                    result += word.checkResult()
                }
            }
            method.instructions
        }

        return result
    }

}