package dev.kosmx.needle.core

import dev.kosmx.needle.database.ClassMatch
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode


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
                    result += word.checkResult().copy(node = node.name ,method = "${method.name};${method.desc}")
                }
            }
            method.instructions
        }

        return result
    }

}