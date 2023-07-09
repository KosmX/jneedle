package dev.kosmx.needle.scanner

import dev.kosmx.needle.matcher.IJarMatcher
import me.coley.cafedude.classfile.ClassFile
import me.coley.cafedude.io.ClassFileReader
import me.coley.cafedude.io.ClassFileWriter
import me.coley.cafedude.transform.IllegalStrippingTransformer
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.SKIP_DEBUG
import org.objectweb.asm.tree.ClassNode
import java.io.IOException

object ClassScanner {
    fun checkClass(bytes: ByteArray, matchers: List<IJarMatcher>) {

        try {
            val classReader = ClassReader(bytes)

            checkClassNode(classReader, matchers)
        } catch (t: Throwable) {
            try {

                val cr = ClassFileReader()
                val classFile: ClassFile = cr.read(bytes)
                IllegalStrippingTransformer(classFile).transform()
                val classBytes = ClassFileWriter().write(classFile)
                checkClassNode(ClassReader(classBytes), matchers)// + JarCheckMatch(MatchType.POTENTIAL, "unlikely obfuscated")

            } catch (t: Throwable) {
                throw IOException("can't read class file", t)
            }
        }
    }

    private fun checkClassNode(classReader: ClassReader, matchers: List<IJarMatcher>) {
        val classNode = ClassNode()
        classReader.accept(classNode, SKIP_DEBUG)

        val classMatchers = matchers.mapNotNull { it.getClassMatcher(classNode) }

        classNode.methods.forEach {method ->
            classMatchers.forEach {
                it.matchMethod(method, method.instructions.asSequence())
            }
        }

        matchers.forEach { it.postClassMatch() }

    }
}